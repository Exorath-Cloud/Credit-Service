package com.exorathcloud.service.credits.res;

/**
 * Created by toonsev on 12/19/2016.
 */
public class Success {
    private boolean success;
    private String error;

    public Success(boolean success){
        this.success = success;
    }

    public Success(boolean success, String error){
        this(success);
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }
}
