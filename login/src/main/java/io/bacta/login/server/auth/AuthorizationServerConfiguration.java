package io.bacta.login.server.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(WebSecurityConfig.class)
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder oauthClientPasswordEncoder;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("Bacta Admin Console")
                .authorizedGrantTypes("implicit")
                //.autoApprove(true)
                .redirectUris("http://localhost:4200/callback")
                .scopes("all")

                .and()

                //TODO: We will want to add a new client for each galaxy.
                //This client type is for game->login communication.
                .withClient("game")
                .authorizedGrantTypes("client_credentials")
                .secret(oauthClientPasswordEncoder.encode("game-server"))
                .scopes("all");

        //TODO: Add a swgclient->game flow type.
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(oauthClientPasswordEncoder);
    }

    @Bean
    public TokenStore tokenStore() throws NoSuchAlgorithmException {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() throws NoSuchAlgorithmException {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        final KeyPair keyPair = getKeyPair();

        converter.setKeyPair(keyPair);

        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() throws NoSuchAlgorithmException {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }


    //TODO: Implement loading/saving of keys.

    private static final String KEY_ALGORITHM = "RSA";
    private static final String CONFIG_DIRECTORY = "config";
    private static final String KEY_FILENAME = "login.key";
    private static final int KEY_SIZE = 512;

    private KeyPair getKeyPair() throws NoSuchAlgorithmException {

//        final Path keyPath = Paths.get(CONFIG_DIRECTORY, KEY_FILENAME);
//        final File keyFile = keyPath.toFile();
        final KeyPair keyPair = generateKeyPair();

        return keyPair;
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException  {
        final KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keygen.initialize(KEY_SIZE);

        return keygen.generateKeyPair();
    }
}
