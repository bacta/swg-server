package com.ocdsoft.bacta.swg.login.repository;

import com.ocdsoft.bacta.swg.login.entity.ClusterData;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by crush on 7/2/2017.
 */
public interface ClusterRepository extends CrudRepository<ClusterData, Integer> {
    ClusterData findByName(String name);
}
