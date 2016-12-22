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

import com.exorath.service.commons.mongoProvider.MongoProvider;
import com.exorath.service.commons.portProvider.PortProvider;
import com.exorath.service.commons.tableNameProvider.TableNameProvider;
import com.exorathcloud.service.credits.impl.MongoDatabaseProvider;
import com.exorathcloud.service.credits.impl.SimpleService;

/**
 * Created by toonsev on 12/22/2016.
 */
public class Main {
    private  Service service;
    public Main(){
        DatabaseProvider dbProvider = new MongoDatabaseProvider(MongoProvider.getEnvironmentMongoProvider(), TableNameProvider.getEnvironmentTableNameProvider("DB_NAME"));
        service = new SimpleService(dbProvider, MinimumCreditsProvider.getSimpleProvider(0));
        Transport.setup(service, PortProvider.getEnvironmentPortProvider());
    }
    public static void main(String[] args) {
        new Main();
    }
}
