package io.bacta.login.server.model;

import io.bacta.login.message.LoginClusterStatus;

public enum GalaxyPopulationStatus {
    VERY_LIGHT,
    LIGHT,
    MEDIUM,
    HEAVY,
    VERY_HEAVY,
    EXTREMELY_HEAVY,
    FULL;

    public static GalaxyPopulationStatus from(LoginClusterStatus.ClusterData.PopulationStatus status) {
        if (LoginClusterStatus.ClusterData.PopulationStatus.VERY_LIGHT.equals(status)) {
            return VERY_LIGHT;
        } else if (LoginClusterStatus.ClusterData.PopulationStatus.LIGHT.equals(status)) {
            return LIGHT;
        } else if (LoginClusterStatus.ClusterData.PopulationStatus.MEDIUM.equals(status)) {
            return MEDIUM;
        } else if (LoginClusterStatus.ClusterData.PopulationStatus.HEAVY.equals(status)) {
            return HEAVY;
        } else if (LoginClusterStatus.ClusterData.PopulationStatus.VERY_HEAVY.equals(status)) {
            return VERY_HEAVY;
        } else if (LoginClusterStatus.ClusterData.PopulationStatus.EXTREMELY_HEAVY.equals(status)) {
            return EXTREMELY_HEAVY;
        } else {
            return FULL;
        }
    }

    public LoginClusterStatus.ClusterData.PopulationStatus toClusterStatusPopulation() {
        switch (this) {
            case VERY_LIGHT:
                return LoginClusterStatus.ClusterData.PopulationStatus.VERY_LIGHT;
            case LIGHT:
                return LoginClusterStatus.ClusterData.PopulationStatus.LIGHT;
            case MEDIUM:
                return LoginClusterStatus.ClusterData.PopulationStatus.MEDIUM;
            case HEAVY:
                return LoginClusterStatus.ClusterData.PopulationStatus.HEAVY;
            case VERY_HEAVY:
                return LoginClusterStatus.ClusterData.PopulationStatus.VERY_HEAVY;
            case EXTREMELY_HEAVY:
                return LoginClusterStatus.ClusterData.PopulationStatus.EXTREMELY_HEAVY;
            default:
            case FULL:
                return LoginClusterStatus.ClusterData.PopulationStatus.FULL;
        }
    }
}
