package com.ocdsoft.bacta.db.je;

import com.ocdsoft.bacta.engine.db.Repository;
import com.ocdsoft.bacta.swg.login.db.ClusterAlreadyExistsException;
import com.ocdsoft.bacta.swg.login.db.ClusterRepository;
import com.ocdsoft.bacta.swg.login.object.ClusterData;
import com.ocdsoft.bacta.swg.login.object.ClusterListEntry;

import java.util.List;

/**
 * Created by kyle on 4/2/2017.
 */

@Repository
public final class JeClusterRepository implements ClusterRepository {


    @Override
    public ClusterListEntry create(ClusterListEntry clusterListEntry) throws ClusterAlreadyExistsException {
        return null;
    }

    @Override
    public ClusterListEntry update(ClusterListEntry clusterListEntry) {
        return null;
    }

    @Override
    public List<ClusterListEntry> get() {
        return null;
    }

    @Override
    public ClusterListEntry get(int clusterId) {
        return null;
    }

    @Override
    public ClusterListEntry get(String clusterName) {
        return null;
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(int clusterId) {

    }

    @Override
    public void delete(String clusterName) {

    }
}
