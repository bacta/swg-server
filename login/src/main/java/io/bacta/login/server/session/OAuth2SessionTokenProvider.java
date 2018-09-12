package io.bacta.login.server.session;

import com.sun.tools.javac.util.List;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class OAuth2SessionTokenProvider implements SessionTokenProvider {
    private final String tokenUri;
    private final String clientId;
    private final String clientSecret;

    public OAuth2SessionTokenProvider(String tokenUri, String clientId, String clientSecret) {
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public SessionToken Provide(String username, String password) {
        final ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setAccessTokenUri(tokenUri);
        resource.setClientId(clientId);
        resource.setClientSecret(clientSecret);
        resource.setGrantType("password");
        resource.setScope(List.of("all"));
        resource.setUsername(username);
        resource.setPassword(password);
        resource.setClientAuthenticationScheme(AuthenticationScheme.header);

        final OAuth2RestOperations restTemplate = new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext());
        final OAuth2AccessToken token =  restTemplate.getAccessToken();

        return new SessionToken(1, token.getValue());
    }
}
