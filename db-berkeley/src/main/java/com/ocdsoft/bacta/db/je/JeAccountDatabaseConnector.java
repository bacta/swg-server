package com.ocdsoft.bacta.db.je;

import com.ocdsoft.bacta.swg.login.db.AccountDatabaseConnector;
import com.ocdsoft.bacta.swg.login.object.SoeAccount;

import java.util.Set;

/**
 * Created by kyle on 4/2/2017.
 */
public final class JeAccountDatabaseConnector implements AccountDatabaseConnector {
    @Override
    public <T> void createObject(String key, T object) {

    }

    @Override
    public <T> void updateObject(String key, T object) {

    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public SoeAccount lookupSession(String authToken) {
        return null;
    }

    @Override
    public Set<String> getClusterCharacterSet(int clusterId) {
        return null;
    }

    @Override
    public int nextClusterId() {
        return 0;
    }

    @Override
    public int nextAccountId() {
        return 0;
    }
}
