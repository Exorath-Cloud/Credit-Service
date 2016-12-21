package com.exorathcloud.service.credits.res;

/**
 * Created by toonsev on 12/21/2016.
 */
public class Account {
    private String accountId;
    private Long balance;
    private String[] pendingTransactionIds;
    private boolean hasMorePendingTransactions;

    public String getAccountId() {
        return accountId;
    }

    public Long getBalance() {
        return balance;
    }

    public String[] getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public boolean hasMorePendingTransactions() {
        return hasMorePendingTransactions;
    }
}
