/*
 * Copyright 2016 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorathcloud.service.credits.impl;

import com.exorathcloud.service.credits.DatabaseProvider;
import com.exorathcloud.service.credits.MinimumCreditsProvider;
import com.exorathcloud.service.credits.Service;
import com.exorathcloud.service.credits.res.Account;
import com.exorathcloud.service.credits.res.TransactionState;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by toonsev on 12/19/2016.
 */
public class SimpleService implements Service {
    private DatabaseProvider databaseProvider;
    private MinimumCreditsProvider minimumCreditsProvider;

    public SimpleService(DatabaseProvider databaseProvider, MinimumCreditsProvider minimumCreditsProvider) {
        this.databaseProvider = databaseProvider;
        this.minimumCreditsProvider = minimumCreditsProvider;
    }

    public Success incrementUnsafe(String accountId, long amount) {
        Long minimum = null;
        if (amount < 0)//Make sure the account has enough credits
            minimum = getMinimum(accountId);
        return databaseProvider.unsafeIncrement(accountId, amount, minimum);
    }

    public Transaction increment(String accountId, long amount) {
        String transactionId = UUID.randomUUID().toString();
        return increment(accountId, amount, transactionId);
    }

    public Transaction increment(String accountId, long amount, String transactionId) {
        Long minimum = null;//if amount > 0 this will stay null
        if (amount < 0) {//Check if the account has enough credits, return failed transaction if not sufficient
            Long creditsLong = getCredits(accountId);
            long credits = creditsLong == null ? 0l : creditsLong;
            minimum = minimumCreditsProvider.getMinimumCredits(accountId);
            if (credits + amount < minimum)
                return new Transaction(transactionId, accountId, TransactionState.NOT_CREATED, null, amount);
        }

        boolean result = handleTransaction(accountId, transactionId, amount, minimum);
        if (result == true)
            return new Transaction(transactionId, accountId, TransactionState.COMPLETED, Calendar.getInstance().getTime(), amount);//return completed state
        else
            return databaseProvider.getTransaction(transactionId);
    }

    @Override
    public Account getAccount(String accountId) {
        Account account = databaseProvider.getAccount(accountId, 10);
        if (account == null)
            return null;
        if (account.getPendingTransactionIds().size() > 0) {
            long minimum = getMinimum(accountId);
            int i = 0;
            for (String transactionId : account.getPendingTransactionIds()) {
                i++;
                if(i > 20) {
                    account.setHasMorePendingTransactions(true);
                    break;
                }
                long maxTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15);
                Transaction transaction = databaseProvider.getTransaction(transactionId);
                if (transaction.getLastUpdate().getTime() < maxTime) {
                    handleTransaction(accountId, transactionId, transaction.getAmount(), minimum);
                    account.setBalance(account.getBalance() + transaction.getAmount());
                    account.getPendingTransactionIds().remove(transaction.getTransactionId());
                }
            }
        }
        //TODO: Start async process
        return account;
    }

    public Long getCredits(String accountId) {
        Account account = getAccount(accountId);
        return account == null ? null : account.getBalance();
    }

    private boolean handleTransaction(String accountId, String transactionId, long amount, Long minimum) {
        //create new pending transaction in [transactions]
        TransactionState created = databaseProvider.putTransaction(TransactionState.NOT_CREATED, new Transaction(transactionId, accountId, TransactionState.PENDING, Calendar.getInstance().getTime(), amount));
        //add transaction to [users]transactions
        if (created == TransactionState.PENDING)
            databaseProvider.putPendingTransactionInAccount(accountId, transactionId);
        //Change transaction state to applied
        if (databaseProvider.putTransaction(TransactionState.PENDING, new Transaction(transactionId, accountId, TransactionState.APPLIED, Calendar.getInstance().getTime(), amount)) != TransactionState.APPLIED)
            return false;
        //update credits & remove transactionId from [users] (if transactionId is present!)
        Success incremented = databaseProvider.safeIncrement(accountId, transactionId, amount, minimum);
        TransactionState state = incremented.isSuccess() ? TransactionState.COMPLETED : TransactionState.CANCELLED;
        //Update [transactions] to completed
        databaseProvider.putTransaction(TransactionState.APPLIED, new Transaction(transactionId, accountId, state, Calendar.getInstance().getTime(), amount));
        return incremented.isSuccess();
    }

    private long getMinimum(String accountId) {
        return minimumCreditsProvider.getMinimumCredits(accountId);
    }
}
