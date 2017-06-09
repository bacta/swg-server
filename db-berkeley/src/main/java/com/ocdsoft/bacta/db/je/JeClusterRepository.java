package com.ocdsoft.bacta.db.je;

import com.ocdsoft.bacta.engine.io.db.Repository;
import com.ocdsoft.bacta.swg.login.db.ClusterRepository;
import com.ocdsoft.bacta.swg.login.object.ClusterData;

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
    public void delete(Iterable<? extends ClusterData> entries) {

    }

    @Override
    public void delete(ClusterData entry) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public boolean exists(Short aShort) {
        return false;
    }

    @Override
    public Iterable<ClusterData> findAll() {
        return null;
    }

    @Override
    public Iterable<ClusterData> findAll(Iterable<Short> shorts) {
        return null;
    }

    @Override
    public ClusterData findOne(Short aShort) {
        return null;
    }

    @Override
    public <S extends ClusterData> Iterable<S> save(Iterable<S> entries) {
        return null;
    }

    @Override
    public <S extends ClusterData> S save(S entry) {
        return null;
    }
}
