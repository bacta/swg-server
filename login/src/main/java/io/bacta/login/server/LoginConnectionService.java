package io.bacta.login.server;


import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public final class LoginConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginConnectionService.class);

    private final ActorSystem actorSystem;

    public LoginConnectionService(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @PreDestroy
    public void cleanupActorSystem() {
        LOGGER.info("Terminating actor system.");

        actorSystem.terminate();
    }
}
