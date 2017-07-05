package bacta.io.login.server.repository;

import bacta.io.login.server.entity.Account;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by crush on 7/2/2017.
 */
public interface AccountRepository extends CrudRepository<Account, Integer> {
    Account findByUsername(String username);
}
