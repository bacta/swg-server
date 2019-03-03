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
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.actor.galaxy.GalaxyActor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;


@Configuration
@ConfigurationProperties
@Slf4j
@Profile("Galaxy")
public class GalaxyConfiguration {

    private final SpringAkkaExtension ext;
    private ActorSystem actorSystem;

    @Inject
    public GalaxyConfiguration(final ActorSystem actorSystem, final SpringAkkaExtension ext) {
        this.ext = ext;
        this.actorSystem = actorSystem;
        deployActors();
    }

    private void deployActors() {
        actorSystem.actorOf(ext.props(GalaxyActor.class), "galaxy");
    }
}
