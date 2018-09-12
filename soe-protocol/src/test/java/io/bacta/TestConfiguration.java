package io.bacta;

import io.bacta.soe.network.dispatch.DefaultGameNetworkMessageDispatcher;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.network.dispatch.GameNetworkMessageDispatcher;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class TestConfiguration {

    @Inject
    @Bean
    public GameNetworkMessageDispatcher getGameNetworkMessageDispatcher(final GameNetworkMessageControllerLoader controllerLoader,
                                                                        final GameNetworkMessageSerializer gameNetworkMessageSerializer,
                                                                        final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter) {

        return new DefaultGameNetworkMessageDispatcher(controllerLoader, gameNetworkMessageSerializer, gameNetworkMessageTemplateWriter);
    }
}
