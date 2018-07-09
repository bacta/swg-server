package io.bacta.login.server.repository;

import io.bacta.login.server.data.BactaAccount;
import org.springframework.data.repository.CrudRepository;

public interface BactaAccountRepository extends CrudRepository<BactaAccount, Integer> {
    BactaAccount findByUsername(String username);
}
