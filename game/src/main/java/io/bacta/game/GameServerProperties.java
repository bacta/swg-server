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

import io.bacta.engine.AkkaProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetAddress;
import java.util.List;


@Data
@Component
@ConfigurationProperties(prefix = "io.bacta.game")
public class GameServerProperties {
    private InetAddress bindAddress;
    private int bindPort;
    private int bindPingPort;
    private String requiredClientVersion;
    private String clientPath;
    private String clientIniFile;
    private String serverPath;
    private List<Scene> scenes;
    private final AkkaProperties akka;

    @Inject
    public GameServerProperties(final AkkaProperties akkaProperties) {
        this.akka = akkaProperties;
    }

    public String getGalaxyName() {
        return akka.getClusterName();
    }

    @Data
    public static class Scene {
        private String name;
        private String iffPath;
    }
}


