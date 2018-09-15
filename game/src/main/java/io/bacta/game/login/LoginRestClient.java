package io.bacta.game.login;

import io.bacta.galaxy.message.GalaxyServerStatus;

import java.io.UnsupportedEncodingException;

/**
 * Client for communicating with the configured login cluster.
 */
public interface LoginRestClient {
    void updateStatus(String knownGalaxyName, GalaxyServerStatus status) throws UnsupportedEncodingException;
    //void notifyCharacterCreated();
}