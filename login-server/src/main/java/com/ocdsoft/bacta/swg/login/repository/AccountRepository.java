package com.ocdsoft.bacta.swg.login.repository;

import com.ocdsoft.bacta.swg.login.entity.Account;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by crush on 7/2/2017.
 */
public interface AccountRepository extends CrudRepository<Account, Integer> {
    Account findByUsername(String username);
}
