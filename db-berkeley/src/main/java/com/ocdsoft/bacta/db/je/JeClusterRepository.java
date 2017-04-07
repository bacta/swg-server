package com.ocdsoft.bacta.db.je;

import com.ocdsoft.bacta.engine.db.Repository;
import com.ocdsoft.bacta.swg.login.db.ClusterRepository;
import com.ocdsoft.bacta.swg.login.object.ClusterServerEntry;
import com.ocdsoft.bacta.swg.login.object.SoeAccount;

import java.util.Set;

/**
 * Created by kyle on 4/2/2017.
 */

@Repository
public final class JeClusterRepository implements ClusterRepository {

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Short aShort) {

    }

    @Override
    public void delete(Iterable<? extends ClusterServerEntry> entries) {

    }

    @Override
    public void delete(ClusterServerEntry entry) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public boolean exists(Short aShort) {
        return false;
    }

    @Override
    public Iterable<ClusterServerEntry> findAll() {
        return null;
    }

    @Override
    public Iterable<ClusterServerEntry> findAll(Iterable<Short> shorts) {
        return null;
    }

    @Override
    public ClusterServerEntry findOne(Short aShort) {
        return null;
    }

    @Override
    public <S extends ClusterServerEntry> Iterable<S> save(Iterable<S> entries) {
        return null;
    }

    @Override
    public <S extends ClusterServerEntry> S save(S entry) {
        return null;
    }
}
