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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.bacta.actor.ActorConstants;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.login.server.actor.LoginSupervisor;
import io.bacta.login.server.session.OAuth2SessionTokenProvider;
import io.bacta.login.server.session.SessionTokenProvider;
import io.bacta.soe.network.connection.ConnectionMap;
import io.bacta.soe.network.connection.DefaultConnectionMap;
import io.bacta.soe.network.dispatch.DefaultGameNetworkMessageDispatcher;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.network.dispatch.GameNetworkMessageDispatcher;
import io.bacta.soe.network.udp.SoeTransceiver;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.inject.Inject;
import java.util.concurrent.Executor;

/**
 * Created by kyle on 4/12/2016.
 */

@Slf4j
@Configuration
@EnableScheduling
@ConfigurationProperties
public class LoginServerConfiguration implements SchedulingConfigurer {

    private final LoginServerProperties loginServerProperties;
    private final SpringAkkaExtension ext;
    private ActorSystem actorSystem;
    private ActorRef loginSupervisor;

    @Inject
    public LoginServerConfiguration(final LoginServerProperties loginServerProperties, final SpringAkkaExtension ext) {
        this.loginServerProperties = loginServerProperties;
        this.ext = ext;
    }

    @Inject
    @Bean
    public ActorSystem getActorSystem(final ApplicationContext context){
        // Create an Akka system
        actorSystem = ActorSystem.create("Galaxy", akkaConfiguration());
        ext.initialize(context);

        // Start root actor
        loginSupervisor = actorSystem.actorOf(ext.props(LoginSupervisor.class), ActorConstants.LOGIN_SUPERVISOR);
        return actorSystem;
    }

    private Config akkaConfiguration() {
        return ConfigFactory.load(loginServerProperties.getAkka().getConfig());
    }


    @Bean(name = "LoginConnectionMap")
    public ConnectionMap getConnectionCache() {
        return new DefaultConnectionMap();
    }

    @Inject
    @Bean
    public GameNetworkMessageDispatcher getGameNetworkMessageDispatcher(final GameNetworkMessageControllerLoader controllerLoader,
                                                                        final GameNetworkMessageSerializer gameNetworkMessageSerializer,
                                                                        final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter) {

        return new DefaultGameNetworkMessageDispatcher(controllerLoader, gameNetworkMessageSerializer, gameNetworkMessageTemplateWriter);
    }

    @Inject
    @Bean(name = "LoginTransceiver")
    public SoeTransceiver startTransceiver(final SoeTransceiver soeTransceiver, @Qualifier("LoginConnectionMap") ConnectionMap connectionMap) {
        LOGGER.info("Starting LoginTransceiver");
        connectionMap.setGetConnectionMethod(soeTransceiver::getConnection);
        connectionMap.setBroadcastMethod(soeTransceiver::broadcast);
        soeTransceiver.start("login", loginServerProperties.getBindAddress(), loginServerProperties.getBindPort());
        return soeTransceiver;
    }

    @Bean
    public SessionTokenProvider getSessionTokenProvider(LoginServerProperties properties) {
        final LoginServerProperties.OAuthProperties oauth = properties.getOauth();
        final String tokenEndpoint = oauth.getBase() + oauth.getToken(); //TODO: Make this more robust - take separators into account.
        return new OAuth2SessionTokenProvider(tokenEndpoint, oauth.getClientId(), oauth.getClientSecret());
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler());
    }
}
