package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.OAuthToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static com.orbvpn.api.domain.OAuthConstants.*;

@Service
public class TokenService {

    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private UriComponentsBuilder builder;

    public String getAmazonToken(String code) {
        return null;
    }

    public String getLinkedinToken(String code) {
        return null;
    }

    public String getTwitterToken(String code) {
        return null;
    }

    public String getAppleToken(String code) {
        return null;
    }

    public String getFacebookToken(String code) {
        return null;
    }

    public String getGoogleToken(String code) {

        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        builder = UriComponentsBuilder.fromHttpUrl(googleTokenURL)
                .queryParam("code",code)
                .queryParam("client_id",googleClientId)
                .queryParam("client_secret",googleClientSecret)
                .queryParam("redirect_uri",googleRedirectURL)
                .queryParam("grant_type","authorization_code");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<OAuthToken> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                OAuthToken.class);

        return Objects.requireNonNull(response.getBody()).getId_token();
    }
}
