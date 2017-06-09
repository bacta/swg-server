package com.ocdsoft.bacta.swg.login.db;

/**
 * Created by crush on 6/8/2017.
 */
public final class ClusterAlreadyExistsException extends Exception {
    public ClusterAlreadyExistsException(final String existingClusterName) {
        super(String.format("A cluster already exists with the name '%s'.",
                existingClusterName));
    }
}
