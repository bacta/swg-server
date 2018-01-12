package io.bacta.login.server.repository;

import io.bacta.login.server.data.GalaxyRecord;
import org.springframework.data.repository.CrudRepository;

public interface GalaxyRepository extends CrudRepository<GalaxyRecord, Integer> {
    GalaxyRecord findByName(String name);
}
