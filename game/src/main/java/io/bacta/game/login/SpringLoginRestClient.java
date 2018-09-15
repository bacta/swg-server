package io.bacta.game.login;

import io.bacta.galaxy.message.GalaxyServerStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Service
public class SpringLoginRestClient implements LoginRestClient {
    private final RestTemplateBuilder restTemplateBuilder;

    @Inject
    public SpringLoginRestClient(RestTemplateBuilder restTemplateBuilder,
                                 @Value("${io.bacta.game.login.rest.base}") String loginBaseAddress) {
        this.restTemplateBuilder = restTemplateBuilder.rootUri(loginBaseAddress);
    }

    @Override
    public void updateStatus(String knownGalaxyName, GalaxyServerStatus status) throws UnsupportedEncodingException {
        try {
            final String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhbGwiXSwiZXhwIjoxNTM2OTk2MjA1LCJqdGkiOiI1MTBkODhlMy1lMGU3LTQyYmUtYmJmMS00NTQxODNmNTFjZmUiLCJjbGllbnRfaWQiOiJnYW1lIn0.dH7Db28gBwO7opV8ogeTYADoVQjLEGsxIGya_F81Bk1tnSisPI9LrbJP4_ALSrApFpPAOynzRXRSVcTscep5fw";
            final String endpoint = String.format("/galaxies/%s", URLEncoder.encode(status.getName(), "UTF-8"));
            final RestTemplate restTemplate = restTemplateBuilder.build();

            final HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            final HttpEntity<GalaxyServerStatus> entity = new HttpEntity<>(status, headers);
            final ResponseEntity<String> response = restTemplate.exchange("/galaxies/{name}", HttpMethod.PUT, entity, String.class, status.getName());
        } catch (HttpServerErrorException ex) {
            LOGGER.error(ex.getMessage());
        } catch (ResourceAccessException ex) {
            LOGGER.error("Could not update login cluster with status: {}", ex.getMessage());
        }
    }
}
