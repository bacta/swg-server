package io.bacta.login.server.repository;

import io.bacta.login.server.model.Galaxy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalaxyRepository extends CrudRepository<Galaxy, Integer> {
    Galaxy findByName(String name);

    Galaxy findByAddressAndPort(String address, int port);
}
