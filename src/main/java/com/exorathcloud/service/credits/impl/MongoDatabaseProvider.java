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

package com.exorathcloud.service.credits.impl;

import com.exorath.service.commons.mongoProvider.MongoProvider;
import com.exorath.service.commons.tableNameProvider.TableNameProvider;
import com.exorathcloud.service.credits.DatabaseProvider;
import com.exorathcloud.service.credits.res.Account;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;
import com.exorathcloud.service.credits.res.TransactionState;
import com.google.gson.Gson;
import com.mongodb.DuplicateKeyException;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.UpdateOptions;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.Arrays;


/**
 * Created by toonsev on 12/22/2016.
 */
public class MongoDatabaseProvider implements DatabaseProvider {
    private final Morphia morphia = new Morphia();

    {
        morphia.mapPackage("com.exorathcloud.service.credits.res");
    }

    private Datastore datastore;

    public MongoDatabaseProvider(MongoProvider mongoProvider, TableNameProvider databaseNameProvider) {
        datastore = morphia.createDatastore(mongoProvider.getClient(), databaseNameProvider.getTableName());
        datastore.ensureIndexes();
    }

    @Override
    public Transaction getTransaction(String transactionId) {
        return datastore.get(Transaction.class, transactionId);
    }

    @Override
    public Success unsafeIncrement(String accountId, long amount, Long minimum) {
        Query<Account> query = datastore.createQuery(Account.class).field(Mapper.ID_KEY).equal(accountId);
        UpdateOperations<Account> ops = datastore.createUpdateOperations(Account.class).inc("balance", amount);
        UpdateOptions opts = new UpdateOptions();
        if (minimum == null)
            opts.upsert(true);
        else if (amount < 0)
            query = query.field("balance").greaterThanOrEq(minimum - amount);

        UpdateResults res = datastore.update(query, ops, opts);
        return res.getUpdatedCount() > 0 || res.getInsertedCount() > 0 ? new Success(true) : new Success(false, "Not enough credits or document does not exist");
    }

    @Override
    public Success safeIncrement(String accountId, String transactionId, long amount, Long minimum) {
        Query<Account> query = datastore.createQuery(Account.class).field(Mapper.ID_KEY).equal(accountId)
                .field(Account.PENDING_TRANSACTIONS_KEY).equal(transactionId);
        UpdateOperations<Account> ops = datastore.createUpdateOperations(Account.class)
                .inc("balance", amount)
                .removeAll(Account.PENDING_TRANSACTIONS_KEY, transactionId);


        UpdateResults res = datastore.update(query, ops);
        return res.getUpdatedCount() > 0 ? new Success(true) : new Success(false, "account did not contain transaction");
    }

    @Override
    public TransactionState putTransaction(TransactionState requiredState, Transaction transaction) {
        Query<Transaction> query = datastore.createQuery(Transaction.class).field(Mapper.ID_KEY).equal(transaction.getTransactionId());
        UpdateOperations<Transaction> ops = datastore.createUpdateOperations(Transaction.class)
                .setOnInsert(Transaction.ACCOUNT_ID_KEY, transaction.getAccountId())
                .setOnInsert(Transaction.AMOUNT_KEY, transaction.getAmount())
                .set(Transaction.STATE_KEY, transaction.getTransactionState())
                .set(Transaction.LAST_UPDATE_KEY, transaction.getLastUpdate());
        UpdateOptions options = new UpdateOptions();
        if (requiredState == TransactionState.NOT_CREATED) {
            options = options.upsert(true);
            query = query.field(Transaction.STATE_KEY).doesNotExist();
        } else
            query.field(Transaction.STATE_KEY).equal(requiredState);

        try {
            UpdateResults res = datastore.update(query, ops, options);
            return res.getUpdatedCount() > 0 || res.getInsertedCount() > 0 == true ? transaction.getTransactionState() : null;
        }catch(DuplicateKeyException e){
            return null;
        }
    }

    @Override
    public boolean putPendingTransactionInAccount(String accountId, String transactionId) {
        Query<Account> query = datastore.createQuery(Account.class).field(Mapper.ID_KEY).equal(accountId).field(Account.PENDING_TRANSACTIONS_KEY).notIn(Arrays.asList(new String[]{transactionId}));
        UpdateOperations<Account> ops = datastore.createUpdateOperations(Account.class).push(Account.PENDING_TRANSACTIONS_KEY, transactionId);
        UpdateOptions opts = new UpdateOptions().upsert(true);
        UpdateResults res = datastore.update(query, ops, opts);
        return res.getUpdatedCount() > 0 || res.getInsertedCount() > 0;
    }

    @Override
    public Account getAccount(String accountId, int pendingTransactionsBatch) {
        return datastore.find(Account.class).field(Mapper.ID_KEY).equal(accountId).get();
    }
}
