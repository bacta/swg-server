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

package io.bacta.galaxy.server.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.galaxy.server.actor.GalaxyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;


@Configuration
@ConfigurationProperties
@Slf4j
public class GalaxyServerConfiguration {

    private final GalaxyServerProperties galaxyServerProperties;
    private final SpringAkkaExtension ext;

    @Inject
    public GalaxyServerConfiguration(final GalaxyServerProperties galaxyServerProperties, final SpringAkkaExtension ext) {
        this.galaxyServerProperties = galaxyServerProperties;
        this.ext = ext;
    }

    @Inject
    @Bean
    public ActorSystem getActorSystem(final ApplicationContext context){
        // Create an Akka system
        ActorSystem system = ActorSystem.create("GalaxyCluster", akkaConfiguration());
        ext.initialize(context);
        return system;
    }

    private Config akkaConfiguration() {
        return ConfigFactory.load(galaxyServerProperties.getAkka().getConfig());
    }

    @Inject
    public ActorRef getGalaxyManager(final ActorSystem actorSystem) {
        return actorSystem.actorOf(ext.props(GalaxyManager.class), "galaxyManager");
    }
}
