package com.exorathcloud.service.credits.impl;

import com.exorathcloud.service.credits.DatabaseProvider;
import com.exorathcloud.service.credits.MinimumCreditsProvider;
import com.exorathcloud.service.credits.Service;
import com.exorathcloud.service.credits.res.Account;
import com.exorathcloud.service.credits.res.TransactionState;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;

import java.util.Calendar;
import java.util.Date;
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
            long credits = getCredits(accountId);
            minimum = minimumCreditsProvider.getMinimumCredits(accountId);
            if (getCredits(accountId) < minimum)
                return new Transaction(accountId, TransactionState.NOT_CREATED, null, amount);
        }
        handleTransaction(accountId, transactionId, amount, minimum);

        return new Transaction(accountId, TransactionState.COMPLETED, Calendar.getInstance().getTime(), amount);//return completed state
    }

    public Long getCredits(String accountId) {
        Account account = databaseProvider.getAccount(accountId, 10);
        if (account == null)
            return null;
        if (account.getPendingTransactionIds().length > 0) {
            long minimum = getMinimum(accountId);
            for (String transactionId : account.getPendingTransactionIds()) {
                long maxTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15);
                Transaction transaction = databaseProvider.getTransaction(transactionId);
                if (transaction.getLastUpdated().getTime() < maxTime)
                    handleTransaction(accountId, transactionId, transaction.getAmount(), minimum);
            }
        }
        //TODO: Start async process
        return account.getBalance();//return credits
    }

    private void handleTransaction(String accountId, String transactionId, long amount, Long minimum) {
        //create new pending transaction in [transactions]
        databaseProvider.putTransaction(TransactionState.NOT_CREATED, new Transaction(accountId, TransactionState.PENDING, Calendar.getInstance().getTime(), amount));//TODO: Handle boolean response
        //add transaction to [users]transactions
        databaseProvider.putPendingTransactionInAccount(accountId, transactionId);
        //Change transaction state to applied
        databaseProvider.putTransaction(TransactionState.PENDING, new Transaction(accountId, TransactionState.APPLIED, Calendar.getInstance().getTime(), amount));//TODO: Handle boolean response
        //update credits & remove transactionId from [users] (if transactionId is present!)
        databaseProvider.safeIncrement(accountId, transactionId, amount, minimum);
        //Update [transactions] to completed
        databaseProvider.putTransaction(TransactionState.APPLIED, new Transaction(accountId, TransactionState.COMPLETED, Calendar.getInstance().getTime(), amount));//TODO: Handle boolean response
    }

    private long getMinimum(String accountId) {
        return minimumCreditsProvider.getMinimumCredits(accountId);
    }
}
