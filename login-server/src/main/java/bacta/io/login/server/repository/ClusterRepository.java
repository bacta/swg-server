package bacta.io.login.server.repository;

import bacta.io.login.server.entity.ClusterData;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by crush on 7/2/2017.
 */
public interface ClusterRepository extends CrudRepository<ClusterData, Integer> {
    ClusterData findByName(String name);
}
