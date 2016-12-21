package com.exorathcloud.service.credits;

import com.exorathcloud.service.credits.res.Account;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;
import com.exorathcloud.service.credits.res.TransactionState;

/**
 * Created by toonsev on 12/19/2016.
 */
public interface DatabaseProvider {

    Transaction getTransaction(String transactionId);

    Success unsafeIncrement(String accountId, long amount, Long minimum);

    Success safeIncrement(String accountId, String transactionId, long amount, Long minimum);

    boolean putTransaction(TransactionState requiredState, Transaction transaction);

    boolean putPendingTransactionInAccount(String accountId, String transactionId);

    /**
     * Gets an account by it's id.
     * Only a limited amount of transactions are returned, the maximum is indicated with the {@param pendingTransactionsBatch}. If there are more transactions {@link Account#hasMorePendingTransactions()} should return true.
     * This should return null in case no account was found and throw an exception when db access failed.
     *
     * @param accountId                the accountId to fetch an account of
     * @param pendingTransactionsBatch the maximum amount of pending transactions that will be returned in the account
     * @return the account instance
     */
    Account getAccount(String accountId, int pendingTransactionsBatch);

}
