package com.exorathcloud.service.credits.impl;

import com.exorathcloud.service.credits.Service;
import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;

import java.util.UUID;

/**
 * Created by toonsev on 12/19/2016.
 */
public class SimpleService implements Service {

    public Success incrementUnsafe(String accountId, long amount) {
        if (amount < 0) {
            //Make sure the account has enough credits
        }
        return null;
    }

    public Transaction increment(String accountId, long amount) {
        String transactionId = UUID.randomUUID().toString();
        return increment(accountId, amount, transactionId);
    }

    public Transaction increment(String accountId, long amount, String transactionId) {
        if (amount < 0) {
            //Check if the account has enough credits, return failed transaction if not sufficient
        }

        //create new pending transaction in [transactions]

        //add transaction to [users]transactions

        //Change transaction state to applied

        //update credits & remove transactionId from [users] (if transactionId is present!)

        //Update [transactions] to completed

        return null;//return completed state
    }

    public Long getCredits(String account) {
        //get [users] account (limit transactions to X)

        //Resolve these transactions synchronously, if there are more: resolve these others async (from last to earliest, if one is already resolved, stop resolving).

        return null;//return credits (todo: return whether or not the value is up to date)
    }
}
