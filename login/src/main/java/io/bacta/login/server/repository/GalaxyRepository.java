package io.bacta.login.server.repository;

import io.bacta.login.server.data.GalaxyRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalaxyRepository extends CrudRepository<GalaxyRecord, Integer> {
    GalaxyRecord findByName(String name);
}
