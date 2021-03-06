/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.game;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.bacta.engine.AkkaProperties;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.engine.conf.ini.IniBactaConfiguration;
import io.bacta.game.galaxy.GalaxyActor;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.network.handler.DefaultGameNetworkMessageHandler;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;


@Configuration
@Slf4j
@Profile("test")
public class GameServerTestConfiguration {

    private final GameServerProperties gameServerProperties;
    private final AkkaProperties akkaProperties;
    private final SpringAkkaExtension ext;
    private ActorSystem actorSystem;
    private ActorRef galaxySupervisor;

    @Inject
    public GameServerTestConfiguration(final GameServerProperties gameServerProperties, final AkkaProperties akkaProperties, final SpringAkkaExtension ext) {
        this.gameServerProperties = gameServerProperties;
        this.akkaProperties = akkaProperties;
        this.ext = ext;
    }

    @Inject
    @Bean
    public ActorSystem getActorSystem(final ApplicationContext context){
        // Create an Akka system
        actorSystem = ActorSystem.create("Galaxy", akkaConfiguration());
        ext.initialize(context);

        // Start root actor
        galaxySupervisor = actorSystem.actorOf(ext.props(GalaxyActor.class), "galaxySupervisor");
        return actorSystem;
    }

    private Config akkaConfiguration() {
        return ConfigFactory.load(akkaProperties.getConfig());
    }

    @Inject
    @Bean
    public GameNetworkMessageHandler getGameNetworkMessageDispatcher(final GameNetworkMessageControllerLoader controllerLoader,
                                                                     final GameNetworkMessageSerializer gameNetworkMessageSerializer) {

        return new DefaultGameNetworkMessageHandler(controllerLoader);
    }

    @Bean
    public BactaConfiguration getBactaConfiguration() throws FileNotFoundException {
        return new IniBactaConfiguration(Paths.get(gameServerProperties.getClientPath() + File.separator + gameServerProperties.getClientIniFile()));
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down actor system {}", actorSystem.name());
        actorSystem.terminate();
    }
}
