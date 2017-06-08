package com.ocdsoft.bacta.db.je;

import com.ocdsoft.bacta.swg.login.object.SoeAccount;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

/**
 * Created by kyle on 3/31/2017.
 */
public class AccountDA {

    PrimaryIndex<String,SoeAccount> usernameIndex;
    SecondaryIndex<Integer, String, SoeAccount> bactaIdIndex;

    SecondaryIndex<Long, String, SoeAccount> characterIndex;

    public AccountDA(EntityStore store)
            throws DatabaseException {

        // Primary key for SoeAccount classes
        usernameIndex = store.getPrimaryIndex(String.class, SoeAccount.class);

        // Secondary key for SoeAccount classes
        // Last field in the getSecondaryIndex() method must be
        // the name of a class member; in this case, an
        // SoeAccount.class data member.
        bactaIdIndex = store.getSecondaryIndex(usernameIndex, Integer.class, "id");
    }
}
