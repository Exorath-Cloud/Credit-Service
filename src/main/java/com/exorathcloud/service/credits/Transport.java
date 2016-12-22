package com.exorathcloud.service.credits;


import com.exorath.service.commons.portProvider.PortProvider;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;
import com.exorathcloud.service.credits.res.TransactionState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import spark.Route;

import java.util.Calendar;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * Created by toonsev on 12/17/2016.
 */
public class Transport {
    private static final Gson GSON = new Gson();

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
                Long credits = service.getCredits(req.params("accountId"));
                return credits == null ? new JsonObject() : credits;
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
