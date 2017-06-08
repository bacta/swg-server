/*
 * Created by IntelliJ IDEA.
 * User: Kyle
 * Date: 4/3/14
 * Time: 8:50 PM
 */
package com.ocdsoft.bacta.swg.login.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ocdsoft.bacta.swg.login.object.SoeAccount;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

@Slf4j
public final class AccountProvider implements Provider<SoeAccount> {

    @Override
    public SoeAccount get() {
        try {
            return new SoeAccount();
        } catch (Exception e) {
            log.error("Unable to create account object", e);
        }

        return null;
    }
}
