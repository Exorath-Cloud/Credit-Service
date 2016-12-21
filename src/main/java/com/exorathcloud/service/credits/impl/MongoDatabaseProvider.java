package com.exorathcloud.service.credits.impl;

import com.exorath.service.commons.mongoProvider.MongoProvider;
import com.exorath.service.commons.tableNameProvider.TableNameProvider;
import com.exorathcloud.service.credits.DatabaseProvider;
import com.exorathcloud.service.credits.res.Account;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;
import com.exorathcloud.service.credits.res.TransactionState;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import static com.mongodb.client.model.Filters.gt;

/**
 * Created by toonsev on 12/22/2016.
 */
public class MongoDatabaseProvider implements DatabaseProvider {
    private static final String USERS_COL_NAME = "users";
    private static final String TRANSACTIONS_COL_NAME = "transactions";

    private static final String ACCOUNT_ID_KEY = "accountId";
    private static final String TRANSACTION_STATE_KEY = "state";
    private static final String LAST_UPDATED_KEY = "lastUpdated";
    private static final String AMOUNT_KEY = "amount";
    private static final String BALANCE_KEY = "balance";

    private MongoClient mongoClient;

    private MongoCollection<Document> usersCollection;
    private MongoCollection<Document> transactionsCollection;

    public MongoDatabaseProvider(MongoProvider mongoProvider, TableNameProvider databaseNameProvider) {
        mongoClient = mongoProvider.getClient();
        MongoDatabase db = mongoClient.getDatabase(databaseNameProvider.getTableName());
        usersCollection = db.getCollection(USERS_COL_NAME);
        transactionsCollection = db.getCollection(TRANSACTIONS_COL_NAME);
    }

    @Override
    public Transaction getTransaction(String transactionId) {
        Document document = transactionsCollection.find(new Document("_id", transactionId)).limit(1).first();
        return document == null ? null : new Transaction(
                document.getString(ACCOUNT_ID_KEY),
                TransactionState.valueOf(document.getString(TRANSACTION_STATE_KEY)),
                document.getDate(LAST_UPDATED_KEY),
                document.getLong(AMOUNT_KEY));
    }

    @Override
    public Success unsafeIncrement(String accountId, long amount, Long minimum) {
        Document requirements = new Document("_id", accountId);
        if (minimum != null)
            requirements.append(BALANCE_KEY, new Document("$gt", minimum));
        Document query = new Document(BALANCE_KEY, new Document("$inc", amount));
        UpdateResult result = usersCollection.updateOne(requirements, query);
        return result.getModifiedCount() == 0 ? new Success(false, "Insufficient balance") : new Success(true);
    }

    @Override
    public Success safeIncrement(String accountId, String transactionId, long amount, Long minimum) {
        return null;
    }

    @Override
    public boolean putTransaction(TransactionState requiredState, Transaction transaction) {
        return false;
    }

    @Override
    public boolean putPendingTransactionInAccount(String accountId, String transactionId) {
        return false;
    }

    @Override
    public Account getAccount(String accountId, int pendingTransactionsBatch) {
        return null;
    }
}
