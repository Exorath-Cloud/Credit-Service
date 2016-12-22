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
            return new Transaction(transactionId, accountId, TransactionState.CANCELLED, Calendar.getInstance().getTime(), amount);
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
                if (transaction.getLastUpdate().getTime() < maxTime)
                    handleTransaction(accountId, transactionId, transaction.getAmount(), minimum);
            }
        }
        //TODO: Start async process
        return account.getBalance();//return credits
    }

    private boolean handleTransaction(String accountId, String transactionId, long amount, Long minimum) {
        //create new pending transaction in [transactions]
        boolean created = databaseProvider.putTransaction(TransactionState.NOT_CREATED, new Transaction(transactionId, accountId, TransactionState.PENDING, Calendar.getInstance().getTime(), amount));
        //add transaction to [users]transactions
        if (created)
            databaseProvider.putPendingTransactionInAccount(accountId, transactionId);
        //Change transaction state to applied
        if (!databaseProvider.putTransaction(TransactionState.PENDING, new Transaction(transactionId, accountId, TransactionState.APPLIED, Calendar.getInstance().getTime(), amount)))
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
