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

import java.util.List;

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
    private List<String> pendingTransactionIds;

    @Transient
    private Boolean hasMorePendingTransactions;

    public String getAccountId() {
        return accountId;
    }

    public Long getBalance() {
        return balance;
    }

    public List<String> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public boolean hasMorePendingTransactions() {
        return hasMorePendingTransactions;
    }

    public void setHasMorePendingTransactions(boolean hasMorePendingTransactions) {
        this.hasMorePendingTransactions = hasMorePendingTransactions;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
