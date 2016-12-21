package com.exorathcloud.service.credits;


import com.exorath.service.commons.portProvider.PortProvider;
import com.exorathcloud.service.credits.res.Transaction;
import com.exorathcloud.service.credits.res.TransactionState;
import com.google.gson.Gson;
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
        post("/accounts/:accountId/inc", getUnsafeIncRoute(service), GSON::toJson);
    }

    public static Route getGetCreditsRoute(Service service) {
        return (req, res) -> service.getCredits(req.params("accountId"));
    }

    public static Route getUnsafeIncRoute(Service service) {
        return (req, res) -> service.incrementUnsafe(req.params("accountId"), Long.valueOf(req.queryParams("credits")));
    }

    public static Route getSafeIncRoute(Service service) {

        return (req, res) -> {
            long amount = Long.valueOf(req.queryParams("amount"));
            String accountId = req.queryParams("accountId");
            String transactionId = req.queryParams("transactionId");
            if(amount == 0)
                return new Transaction(accountId, TransactionState.CANCELLED, Calendar.getInstance().getTime(), 0);
            if(transactionId == null){
                return service.increment(accountId, amount);
            }else{
                return service.increment(accountId, amount, transactionId);
            }
        };
    }
}
