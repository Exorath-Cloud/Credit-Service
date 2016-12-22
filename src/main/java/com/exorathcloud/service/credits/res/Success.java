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
