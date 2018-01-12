package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.Account;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.persist.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 6/8/2017.
 */
@Slf4j
public final class BerkeleyDbAccountRepository implements AccountRepository {
    private static final String STORE_NAME = "accounts";

    private final EntityStore accountStore;
    private final PrimaryIndex<Integer, AccountEntity> accountIdAccessor;
    private final SecondaryIndex<String, Integer, AccountEntity> accountUsernameAccessor;

    public BerkeleyDbAccountRepository(final Environment environment) throws DatabaseException {
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);

        accountStore = new EntityStore(environment, STORE_NAME, storeConfig);
        accountIdAccessor = accountStore.getPrimaryIndex(Integer.class, AccountEntity.class);
        accountUsernameAccessor = accountStore.getSecondaryIndex(accountIdAccessor, String.class, AccountEntity.KEY_ACCOUNT_USERNAME);
    }

    public void close() throws DatabaseException {
        accountStore.close();
    }

    @Override
    public Account create(Account account) {
        try {
            final AccountEntity entity = new AccountEntity(account);
            accountIdAccessor.putNoOverwrite(entity);
            return mapAccountEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not create account with username %s in database.",
                    account.getUsername()), ex);
            return null;
        }
    }

    @Override
    public Account update(Account account) {
        try {
            final AccountEntity entity = new AccountEntity(account);
            accountIdAccessor.put(entity);
            return mapAccountEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not update account with username %s in database.",
                    account.getUsername()), ex);
            return null;
        }
    }

    @Override
    public List<Account> get() {
        try {
            final List<Account> entries = new ArrayList<>();

            try (EntityCursor<AccountEntity> entitiesCursor = accountIdAccessor.entities()) {
                entitiesCursor.forEach((entity) -> entries.add(mapAccountEntity(entity)));
            }

            return entries;

        } catch (DatabaseException ex) {
            LOGGER.error("Could not get all accounts.", ex);
            return null;
        }
    }

    @Override
    public Account get(int accountId) {
        try {
            final AccountEntity entity = accountIdAccessor.get(accountId);
            return mapAccountEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not get account with id %d.", accountId), ex);
            return null;
        }
    }

    @Override
    public Account get(String username) {
        try {
            final AccountEntity entity = accountUsernameAccessor.get(username);
            return mapAccountEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not get account with username %s.", username), ex);
            return null;
        }
    }

    @Override
    public void delete() {
        try {
            accountStore.truncateClass(AccountEntity.class);
        } catch (DatabaseException ex) {
            LOGGER.error("Could not delete all accounts.");
        }
    }

    @Override
    public void delete(int accountId) {
        try {
            accountIdAccessor.delete(accountId);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not delete account with id %d.", accountId), ex);
        }
    }

    @Override
    public void delete(String username) {
        try {
            accountUsernameAccessor.delete(username);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not delete account with username %s", username), ex);
        }
    }

    private static Account mapAccountEntity(AccountEntity entity) {
        return new Account(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getAdminLevel());
    }
}