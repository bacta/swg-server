package io.bacta.engine.actor;

public interface ActorMessageControllerFactory {
    <T> void handleMessage(T msg);
}
