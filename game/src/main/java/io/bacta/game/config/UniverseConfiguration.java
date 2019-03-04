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

package io.bacta.game.config;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.bacta.actor.ActorConstants;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.engine.conf.ini.IniBactaConfiguration;
import io.bacta.game.GameServerProperties;
import io.bacta.game.galaxy.GalaxyActor;
import io.bacta.soe.network.udp.SoeTransceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;

@Configuration
@Slf4j
public class UniverseConfiguration {

    private final GameServerProperties gameServerProperties;
    private final SpringAkkaExtension ext;
    private ActorSystem actorSystem;

    @Value("${io.bacta.galaxy.name}")
    private String galaxyName;

    @Inject
    public UniverseConfiguration(final GameServerProperties gameServerProperties, final SpringAkkaExtension ext) {
        this.gameServerProperties = gameServerProperties;
        this.ext = ext;
    }

    @Inject
    @Bean
    public ActorSystem getActorSystem(final ApplicationContext context) {
        // Create an Akka system
        Config akkaConfig = akkaConfiguration();
        actorSystem = ActorSystem.create(ActorConstants.ACTOR_SYSTEM_NAME, akkaConfig);
        ext.initialize(context);

        actorSystem.actorOf(ext.props(GalaxyActor.class), galaxyName);

        return actorSystem;
    }

    private Config akkaConfiguration() {
        return ConfigFactory.load(gameServerProperties.getAkka().getConfig());
    }

    @Bean
    public BactaConfiguration getBactaConfiguration() throws FileNotFoundException {
        final Path configPath = Paths.get(gameServerProperties.getClientPath(), gameServerProperties.getClientIniFile());
        return new IniBactaConfiguration(configPath);
    }

    @Inject
    @Bean
    public SoeTransceiver startTransceiver(final SoeTransceiver soeTransceiver) {
        return soeTransceiver;
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down actor system {}", actorSystem.name());
        actorSystem.terminate();
    }
}
