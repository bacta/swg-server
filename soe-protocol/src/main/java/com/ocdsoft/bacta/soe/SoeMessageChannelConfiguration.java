package com.ocdsoft.bacta.soe;

import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.network.dispatch.ClasspathControllerLoader;
import com.ocdsoft.bacta.soe.network.dispatch.GameNetworkMessageDispatcher;
import com.ocdsoft.bacta.soe.serialize.GameNetworkMessageSerializer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Created by kyle on 7/2/2017.
 */
@Configuration
public class SoeMessageChannelConfiguration implements ApplicationContextAware {

    private final MetricRegistry metricRegistry;
    private final SoeNetworkConfiguration networkConfiguration;
    private ApplicationContext applicationContext;

    @Inject
    public SoeMessageChannelConfiguration(final MetricRegistry metricRegistry,
                                          final SoeNetworkConfiguration networkConfiguration) {
        this.metricRegistry = metricRegistry;
        this.networkConfiguration = networkConfiguration;
    }

    @Bean
    @Inject
    public GameNetworkMessageDispatcher getGameNetworkMessageDispatcher(final GameNetworkMessageSerializer gameNetworkMessageSerializer) {
        return new GameNetworkMessageDispatcher(
                new ClasspathControllerLoader(applicationContext),
                gameNetworkMessageSerializer,
                null
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
