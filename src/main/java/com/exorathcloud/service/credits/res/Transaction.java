package com.exorathcloud.service.credits.res;

import org.mongodb.morphia.annotations.*;

import java.util.Date;

/**
 * Created by toonsev on 12/18/2016.
 */
@Entity("transactions")
@Indexes({
        @Index(fields = @Field("accountId")),
        @Index(fields = @Field("state")),
        @Index(fields = @Field("lastUpdate")),
        @Index(fields = @Field("amount")),
})
public class Transaction {
    public static final String ACCOUNT_ID_KEY = "accountId";
    public static final String STATE_KEY = "state";
    public static final String LAST_UPDATE_KEY = "lastUpdate";
    public static final String AMOUNT_KEY = "amount";
    @Id
    private String transactionId;

    @Property(ACCOUNT_ID_KEY)
    private String accountId;

    @Property(STATE_KEY)
    private TransactionState transactionState;

    @Property(LAST_UPDATE_KEY)
    private Date lastUpdate;

    @Property(AMOUNT_KEY)
    private long amount;

    public Transaction(String transactionId, String accountId, TransactionState transactionState, Date lastUpdated, long amount){
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionState = transactionState;
        this.lastUpdate = lastUpdated;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public long getAmount() {
        return amount;
    }
}
