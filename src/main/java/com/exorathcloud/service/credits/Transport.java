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


import com.exorath.service.commons.portProvider.PortProvider;
import com.exorathcloud.service.credits.res.Account;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;
import com.exorathcloud.service.credits.res.TransactionState;
import com.google.gson.*;
import spark.Route;


import java.lang.reflect.Type;
import java.util.Date;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * Created by toonsev on 12/17/2016.
 */
public class Transport {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class,  new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
                    return new JsonPrimitive(date.getTime());
                }
            }).create();

    /**
     * Sets up the http transport to the service
     *
     * @param service service instance
     */
    public static void setup(Service service, PortProvider portProvider) {
        port(portProvider.getPort());

        get("/accounts/:accountId", getGetCreditsRoute(service), GSON::toJson);
        post("/accounts/:accountId/unsafeInc", getUnsafeIncRoute(service), GSON::toJson);
        post("/accounts/:accountId/inc", getSafeIncRoute(service), GSON::toJson);
    }

    public static Route getGetCreditsRoute(Service service) {
        return (req, res) -> {
            try {
                Account account = service.getAccount(req.params("accountId"));
                return account == null ? new JsonObject() : account;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        };
    }

    public static Route getUnsafeIncRoute(Service service) {
        return (req, res) -> {
            try {
                Long amount = Long.valueOf(req.queryParams("amount"));
                if(amount == null)
                    return new Success(false, "no 'amount' query parameter specified.");
                return service.incrementUnsafe(req.params("accountId"), amount);
            } catch (Exception e) {
                e.printStackTrace();
                return new Success(false, e.getMessage());
            }
        };
    }

    public static Route getSafeIncRoute(Service service) {
        return (req, res) -> {
            long amount = Long.valueOf(req.queryParams("amount"));
            String accountId = req.params("accountId");
            String transactionId = req.queryParams("transactionId");
            if (accountId == null || amount == 0)
                return new Transaction(transactionId, accountId, TransactionState.NOT_CREATED, null, 0);
            try {

                if (transactionId == null) {
                    return service.increment(accountId, amount);
                } else {
                    return service.increment(accountId, amount, transactionId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new Transaction(transactionId, accountId, TransactionState.NOT_CREATED, null, amount);
            }
        };
    }
}
