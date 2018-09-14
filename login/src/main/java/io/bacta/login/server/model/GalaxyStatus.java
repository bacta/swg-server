package io.bacta.login.server.model;

import io.bacta.login.message.LoginClusterStatus;

public enum GalaxyStatus {
    DOWN,
    LOADING,
    UP,
    LOCKED,
    RESTRICTED,
    FULL;

    public static GalaxyStatus from(LoginClusterStatus.ClusterData.Status status) {
        if (LoginClusterStatus.ClusterData.Status.LOADING.equals(status)) {
            return LOADING;
        } else if (LoginClusterStatus.ClusterData.Status.UP.equals(status)) {
            return UP;
        } else if (LoginClusterStatus.ClusterData.Status.LOCKED.equals(status)) {
            return LOCKED;
        } else if (LoginClusterStatus.ClusterData.Status.RESTRICTED.equals(status)) {
            return RESTRICTED;
        } else if (LoginClusterStatus.ClusterData.Status.FULL.equals(status)) {
            return FULL;
        } else {
            return DOWN;
        }
    }

    public LoginClusterStatus.ClusterData.Status toClusterDataStatus() {
        switch (this) {
            case LOADING:
                return LoginClusterStatus.ClusterData.Status.LOADING;
            case UP:
                return LoginClusterStatus.ClusterData.Status.UP;
            case LOCKED:
                return LoginClusterStatus.ClusterData.Status.LOCKED;
            case RESTRICTED:
                return LoginClusterStatus.ClusterData.Status.RESTRICTED;
            case FULL:
                return LoginClusterStatus.ClusterData.Status.FULL;
            default:
            case DOWN:
                return LoginClusterStatus.ClusterData.Status.DOWN;
        }
    }
}
