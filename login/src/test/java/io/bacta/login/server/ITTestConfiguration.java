package io.bacta.login.server;

import io.bacta.login.server.session.SessionException;
import io.bacta.login.server.session.SessionToken;
import io.bacta.login.server.session.SessionTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ITTestConfiguration {
    @Bean
    @Primary
    public SessionTokenProvider getSessionTokenProviderIT(LoginServerProperties properties) {
        return new SessionTokenProvider() {
            @Override
            public SessionToken provide(String username, String password) throws SessionException {
                return new SessionToken(1, "TOKENVALUE");
            }
        };
    }
}
