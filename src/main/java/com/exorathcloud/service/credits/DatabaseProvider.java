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

    TransactionState putTransaction(TransactionState requiredState, Transaction transaction);

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
