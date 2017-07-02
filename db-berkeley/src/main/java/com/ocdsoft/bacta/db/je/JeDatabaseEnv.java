package com.ocdsoft.bacta.db.je;

import com.ocdsoft.bacta.engine.context.ShutdownListener;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

/**
 * Created by kyle on 3/31/2017.
 */
@Singleton
@Slf4j
public class JeDatabaseEnv implements ShutdownListener {

    private static boolean initialized = false;
    private Environment myEnv;

    @Inject
    public JeDatabaseEnv() {
        try {
            if(initialized) {
                throw new RuntimeException("Environment already initialized");
            }

            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            envConfig.setTransactional(true);
            myEnv = new Environment(new File("/com.ocdsoft.bacta.db/accounts"), envConfig);
            initialized = true;

        } catch (DatabaseException dbe) {
            LOGGER.error("Unable to created environment", dbe);
            System.exit(1);
        }
    }

    public void setup(File envHome, boolean readOnly)
            throws DatabaseException {

        // Instantiate an environment configuration object
        EnvironmentConfig myEnvConfig = new EnvironmentConfig();
        // Configure the environment for the read-only state as identified
        // by the readOnly parameter on this method call.
        myEnvConfig.setReadOnly(readOnly);
        // If the environment is opened for write, then we want to be
        // able to create the environment if it does not exist.
        myEnvConfig.setAllowCreate(!readOnly);

        // Instantiate the Environment. This opens it and also possibly
        // creates it.
        myEnv = new Environment(envHome, myEnvConfig);
    }

    public Environment getEnv() {
        return myEnv;
    }

    @Override
    public void shutdown() {
        if (myEnv != null) {
            try {
                myEnv.close();
            } catch(DatabaseException dbe) {
                System.err.println("Error closing environment" +
                        dbe.toString());
            }
        }
    }
}
