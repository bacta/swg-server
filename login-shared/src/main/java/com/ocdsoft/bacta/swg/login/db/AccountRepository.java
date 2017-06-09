package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.Account;

import java.util.List;

/**
 * Created by crush on 6/8/2017.
 *
 * CRUD operations on Accounts.
 */
public interface AccountRepository {

    Account create(Account account);
    Account update(Account account);

    List<Account> get();
    Account get(int accountId);
    Account get(String username);

    void delete();
    void delete(int accountId);
    void delete(String username);
}