package com.exorathcloud.service.credits;

import com.exorathcloud.service.credits.res.Success;
import com.exorathcloud.service.credits.res.Transaction;


/**
 * Created by toonsev on 12/17/2016.
 */
public interface Service {
    /**
     * Adds credits to an account with a randomly generated transactionId.
     *
     * @param accountId the account id
     * @param amount    the amount of credits (1 USD = $1.000.000.000)
     * @return the state of this transaction
     */
    Transaction increment(String accountId, long amount);

    /**
     * Adds credits to an account with a pre-defined transaction id, if the transaction id already exists nothing will be created.
     *
     * @param accountId     the account id
     * @param transactionId the id of the transaction
     * @param amount        the amount of credits (1 USD = $1.000.000.000)
     * @return the state of this transaction
     */
    Transaction increment(String accountId, long amount, String transactionId);


    /**
     * Does an unsafe credit incrementation (/deduction), may occur more or less then once according to network partitioning and failure.
     *
     * @param accountId the account id
     * @param amount    the amount of credits to increment (/deduct)
     * @return whether or not this action was successful (and why it failed if so). Note that success means that it's been updated at least once, failure means it's not updated at all.
     */
    Success incrementUnsafe(String accountId, long amount);

    /**
     * Gets the credits of an account, if there are any pending transactions, a merge will be attempted once.
     *
     * @return the amount of credits the specified account has, null if the account does not exist
     */
    Long getCredits(String account);


}
