package com.exorathcloud.service.credits.res;

import org.mongodb.morphia.annotations.*;

/**
 * Created by toonsev on 12/21/2016.
 */
@Entity("accounts")
@Indexes({
        @Index(fields = @Field("balance"))
})
public class Account {
    public static final String BALANCE_KEY = "balance";

    public static final String PENDING_TRANSACTIONS_KEY = "pendingTransactions";
    @Id
    private String accountId;
    @Property(BALANCE_KEY)
    private Long balance;
    @Property(PENDING_TRANSACTIONS_KEY)
    private String[] pendingTransactionIds;

    private transient boolean hasMorePendingTransactions;

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
