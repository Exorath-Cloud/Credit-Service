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
