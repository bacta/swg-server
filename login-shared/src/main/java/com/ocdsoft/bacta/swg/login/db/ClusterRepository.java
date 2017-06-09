package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.ClusterListEntry;

import java.util.List;

/**
 * Created by kburkhardt on 2/23/14.
 *
 * A repository of clusters which the login server is expecting to register with it.
 */
public interface ClusterRepository {
    /**
     * Creates a new cluster list entry in the repository, and returns a new instance of the object
     * that was used to create it, but with the id now initialized to one assigned by the repository.
     * <p>
     * Don't use this method to try and update an existing cluster, as it will throw an exception.
     *
     * @param clusterListEntry The entry which to create in the repository.
     * @return A new instance of the original entry with the id initialized.
     * @throws ClusterAlreadyExistsException If a cluster already exists with the given name or id.
     * @see #update(ClusterListEntry)
     */
    ClusterListEntry create(ClusterListEntry clusterListEntry) throws ClusterAlreadyExistsException;

    /**
     * Updates an existing cluster, or will insert it if it doesn't exist. It is preferred to use the create
     * method to create new entries, however.
     *
     * @param clusterListEntry The entry to update.
     * @return A new copy of this object updated from the repository.
     */
    ClusterListEntry update(ClusterListEntry clusterListEntry);

    /**
     * Gets all clusters from the repository.
     *
     * @return A list of all the clusters.
     */
    List<ClusterListEntry> get();

    /**
     * Gets a specific cluster by its unique id.
     *
     * @param clusterId The id of the cluster to retrieve.
     * @return Returns the cluster for the given id, or null if it doesn't exist.
     */
    ClusterListEntry get(int clusterId);

    /**
     * Gets a specific cluster by its name.
     *
     * @param clusterName The name of the cluster to retrieve.
     * @return Returns the cluster for the given name, or null if it doesn't exist.
     */
    ClusterListEntry get(String clusterName);

    /**
     * Deletes all the clusters from the database.
     */
    void delete();

    /**
     * Deletes a specific cluster by its unique id.
     *
     * @param clusterId The id of the cluster to delete.
     */
    void delete(int clusterId);

    /**
     * Deletes a specific cluster by its name.
     *
     * @param clusterName The name of the cluster to delete. If multiple clusters exist with the name,
     *                    all will be deleted.
     */
    void delete(String clusterName);
}
