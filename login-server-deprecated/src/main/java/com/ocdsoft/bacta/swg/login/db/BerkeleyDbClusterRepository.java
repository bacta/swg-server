package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.ClusterListEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.persist.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 6/7/2017.
 */
@Slf4j
public final class BerkeleyDbClusterRepository implements ClusterRepository {
    private static final String STORE_NAME = "clusters";

    private final EntityStore clusterStore;
    private final PrimaryIndex<Integer, ClusterEntity> clusterIdAccessor;
    private final SecondaryIndex<String, Integer, ClusterEntity> clusterNameAccessor;

    public BerkeleyDbClusterRepository(final Environment environment) throws DatabaseException {
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);

        clusterStore = new EntityStore(environment, STORE_NAME, storeConfig);
        clusterIdAccessor = clusterStore.getPrimaryIndex(Integer.class, ClusterEntity.class);
        clusterNameAccessor = clusterStore.getSecondaryIndex(clusterIdAccessor, String.class, ClusterEntity.KEY_CLUSTER_NAME);
    }

    public void close() throws DatabaseException {
        clusterStore.close();
    }

    @Override
    public ClusterListEntry create(ClusterListEntry clusterListEntry) throws ClusterAlreadyExistsException {
        try {
            final ClusterEntity entity = new ClusterEntity(-1, clusterListEntry.getClusterName());

            if (!clusterIdAccessor.putNoOverwrite(entity))
                throw new ClusterAlreadyExistsException(clusterListEntry.getClusterName());

            return mapClusterEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not create cluster with name %s.", clusterListEntry.getClusterName()), ex);
            return null;
        }
    }

    @Override
    public ClusterListEntry update(ClusterListEntry clusterListEntry) {
        try {
            final ClusterEntity entity = new ClusterEntity(
                    clusterListEntry.getClusterId(),
                    clusterListEntry.getClusterName());

            clusterIdAccessor.put(entity);

            return mapClusterEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not update cluster with id %d and name %s.",
                    clusterListEntry.getClusterId(),
                    clusterListEntry.getClusterName()), ex);
            return null;
        }
    }

    @Override
    public ClusterListEntry get(int clusterId) {
        try {
            final ClusterEntity entity = clusterIdAccessor.get(clusterId);
            return mapClusterEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not get cluster with id %d.", clusterId), ex);
            return null;
        }
    }

    @Override
    public ClusterListEntry get(String clusterName) {
        try {
            final ClusterEntity entity = clusterNameAccessor.get(clusterName);
            return mapClusterEntity(entity);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not get cluster with name %s.", clusterName), ex);
            return null;
        }
    }

    @Override
    public List<ClusterListEntry> get() {
        try {
            final List<ClusterListEntry> entries = new ArrayList<>();

            try (EntityCursor<ClusterEntity> entitiesCursor = clusterIdAccessor.entities()) {
                entitiesCursor.forEach((entity) -> entries.add(mapClusterEntity(entity)));
            }

            return entries;

        } catch (DatabaseException ex) {
            LOGGER.error("Could not get all clusters.",  ex);
            return null;
        }
    }

    @Override
    public void delete() {
        try {
            clusterStore.truncateClass(ClusterEntity.class);
        } catch (DatabaseException ex) {
            LOGGER.error("Could not delete all clusters.");
        }
    }

    @Override
    public void delete(int clusterId) {
        try {
            clusterIdAccessor.delete(clusterId);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not delete cluster with id %d.", clusterId), ex);
        }
    }

    @Override
    public void delete(String clusterName) {
        try {
            clusterNameAccessor.delete(clusterName);
        } catch (DatabaseException ex) {
            LOGGER.error(String.format("Could not delete cluster with name %s", clusterName), ex);
        }
    }

    private static ClusterListEntry mapClusterEntity(ClusterEntity entity) {
        return new ClusterListEntry(entity.getId(), entity.getName());
    }
}