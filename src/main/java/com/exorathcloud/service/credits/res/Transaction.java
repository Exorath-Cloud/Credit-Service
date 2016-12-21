package com.exorathcloud.service.credits.res;

import java.util.Date;

/**
 * Created by toonsev on 12/18/2016.
 */
public class Transaction {
    private String accountId;
    private TransactionState transactionState;
    private Date lastUpdated;
    private long amount;

    public Transaction(String accountId, TransactionState transactionState, Date lastUpdated, long amount){
        this.accountId = accountId;
        this.transactionState = transactionState;
        this.lastUpdated = lastUpdated;
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public long getAmount() {
        return amount;
    }
}
