package com.ocdsoft.bacta.db.je;

import com.ocdsoft.bacta.swg.login.object.SoeAccount;
import com.ocdsoft.bacta.swg.login.service.AccountService;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

import java.io.File;
import java.net.InetAddress;

/**
 * Created by kyle on 3/31/2017.
 */
public class JeAccountService implements AccountService {

    private EntityStore store;

    public JeAccountService() {

        JeDatabaseEnv accountDbEnv = new JeDatabaseEnv();

        try {
            accountDbEnv.setup(new File("com.ocdsoft.bacta.db/account"), false);
            StoreConfig storeConfig = new StoreConfig();

            storeConfig.setAllowCreate(true);

            // Open the environment and entity store
            store = new EntityStore(accountDbEnv.getEnv(), "AccountStore", storeConfig);

        } catch(DatabaseException dbe) {
            // Error code goes here
        } finally {
            accountDbEnv.shutdown();
        }
    }

    @Override
    public boolean authenticate(SoeAccount account, String password) {
        return false;
    }

    @Override
    public SoeAccount validateSession(InetAddress address, String authToken) {
        return null;
    }

    @Override
    public SoeAccount createAccount(String username, String password) {
        return null;
    }

    @Override
    public SoeAccount getAccount(String username) {
        return null;
    }

    @Override
    public void createAuthToken(InetAddress address, SoeAccount account) {

    }

    @Override
    public void updateAccount(SoeAccount account) {

    }
}
