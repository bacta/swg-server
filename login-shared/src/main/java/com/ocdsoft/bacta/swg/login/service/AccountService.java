package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.engine.service.ServiceLifecycle;
import com.ocdsoft.bacta.swg.login.object.SoeAccount;

import java.net.InetAddress;

public interface AccountService extends ServiceLifecycle {

	boolean authenticate(SoeAccount account, String password);

	SoeAccount validateSession(InetAddress address, String authToken);

    SoeAccount createAccount(String username, String password);

    SoeAccount getAccount(String username);

    void createAuthToken(InetAddress address, SoeAccount account);

    void updateAccount(SoeAccount account);
}
