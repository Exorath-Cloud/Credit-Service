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

    public Transaction(){

    }
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
