package io.bacta.engine.actor;

public interface ActorMessageController<T> {
    void receive(T t);
}
