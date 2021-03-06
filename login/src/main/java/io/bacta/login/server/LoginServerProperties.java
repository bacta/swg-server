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

package io.bacta.login.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * Created by kyle on 6/29/2017.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "io.bacta.login.server")
public class LoginServerProperties {
    private InetAddress bindAddress;
    private int bindPort;
    private int maxCharactersPerAccount;
    private boolean autoGalaxyRegistrationEnabled;
    private boolean autoAccountRegistrationEnabled;
    private boolean internalBypassOnlineLimitEnabled;
    private boolean skippingTutorialAllowedForAll;
    private boolean validateClientVersionEnabled;
    private int populationExtremelyHeavyThresholdPercent;
    private int populationVeryHeavyThresholdPercent;
    private int populationHeavyThresholdPercent;
    private int populationMediumThresholdPercent;
    private int populationLightThresholdPercent;
    /**
     * How often the maintenance task for galaxies should run.
     * Time is in milliseconds.
     */
    private long galaxyMaintenanceInterval;
    /**
     * If we haven't heard from a galaxy in the configured time, then we assume it's gone offline.
     * Time is in milliseconds.
     */
    private long galaxyLinkDeadThreshold;

    private OAuthProperties oauth;
    private LoginSessionMode sessionMode;

    @Data
    public static class OAuthProperties {
        private String base;
        private String authorize;
        private String token;
        private String clientId;
        private String clientSecret;
    }
}