package io.bacta.login.server.repository;

import io.bacta.login.server.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    Account findByUsername(String username);
}
