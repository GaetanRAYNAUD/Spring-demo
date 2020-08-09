package com.example.demo.service.google;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.example.demo.common.Constants;
import com.example.demo.config.properties.DemoProperties;
import com.example.demo.service.google.objects.GoogleOpenIdConfigResponseBody;
import com.example.demo.service.google.objects.GoogleOpenIdKeysResponseBody;
import com.example.demo.service.google.objects.GoogleOpenIdMetaData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoogleOpenIdServiceImpl implements GoogleOpenIdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleOpenIdServiceImpl.class);

    private final GoogleOpenIdMetaData metaData = new GoogleOpenIdMetaData();

    private final DemoProperties demoProperties;

    public GoogleOpenIdServiceImpl(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
        this.validateMetaData();
    }

    @Override
    public Claims validateToken(String token) {
        if (token.startsWith(Constants.BEARER)) {
            token = token.substring(Constants.BEARER.length() + 1);
        }

        this.validateMetaData();

        for (PublicKey googleKey : this.metaData.getPublicKeys()) {
            try {
                Iterator<String> issuerIterator = this.metaData.getIssuers().iterator();

                while (issuerIterator.hasNext()) {
                    try {
                        return Jwts.parserBuilder()
                                   .requireIssuer(issuerIterator.next())
                                   .requireAudience(this.demoProperties.getGoogleOpenId().getClientId())
                                   .setSigningKey(googleKey)
                                   .build()
                                   .parseClaimsJws(token)
                                   .getBody();
                    } catch (IncorrectClaimException e) {
                        if (!e.getClaimName().equals(Claims.ISSUER)) {
                            throw e;
                        }
                    }
                }
            } catch (SignatureException ignored) {
            }
        }

        throw new JwtException("Not a valid google token !");
    }

    private synchronized void validateMetaData() {
        LocalDateTime now = LocalDateTime.now();
        RestTemplate restTemplate = new RestTemplate();

        if (this.metaData.getConfigurationExpiration() == null || this.metaData.getConfigurationExpiration().isBefore(now)) {
            ResponseEntity<GoogleOpenIdConfigResponseBody> response = restTemplate.exchange(this.demoProperties.getGoogleOpenId().getConfigurationUrl(),
                                                                                            HttpMethod.GET,
                                                                                            null,
                                                                                            GoogleOpenIdConfigResponseBody.class);
            if (response.getBody() == null) {
                LOGGER.error("Could not get configuration from Google OpenId ! Extending current configuration !");
                this.metaData.setConfigurationExpiration(now.plusHours(1));
            } else {
                GoogleOpenIdConfigResponseBody configuration = response.getBody();
                HttpHeaders headers = response.getHeaders();

                this.metaData.setConfigurationExpiration(Instant.ofEpochMilli(headers.getExpires()).atOffset(ZoneOffset.UTC).toLocalDateTime());

                if (configuration.getJwksUri() != null) {
                    this.metaData.setJwksUri(configuration.getJwksUri());
                }

                if (configuration.getIssuer() != null) {
                    try {
                        this.metaData.setIssuers(Arrays.asList(configuration.getIssuer().toString(), configuration.getIssuer().toURI().getAuthority()));
                    } catch (URISyntaxException e) {
                        LOGGER.error("Could not parse new Google OpenId issuer: {} ! Keeping current !", configuration.getIssuer());
                    }
                }
            }
        }

        if (this.metaData.getKeysExpiration() == null || this.metaData.getKeysExpiration().isBefore(now) && this.metaData.getJwksUri() != null) {
            try {
                ResponseEntity<GoogleOpenIdKeysResponseBody> response = restTemplate.exchange(this.metaData.getJwksUri().toURI(), HttpMethod.GET, null,
                                                                                              GoogleOpenIdKeysResponseBody.class);
                if (response.getBody() == null) {
                    LOGGER.error("Could not get configuration from Google OpenId ! Extending current configuration !");
                    this.metaData.setConfigurationExpiration(now.plusHours(1));
                } else {
                    GoogleOpenIdKeysResponseBody keys = response.getBody();

                    if (keys.getKeys() != null) {
                        List<PublicKey> list = new ArrayList<>();

                        for (Map<String, Object> map : keys.getKeys()) {
                            Jwk jwk = Jwk.fromValues(map);
                            PublicKey publicKey = jwk.getPublicKey();
                            list.add(publicKey);
                        }

                        this.metaData.setPublicKeys(list);
                    }

                    HttpHeaders headers = response.getHeaders();
                    this.metaData.setKeysExpiration(Instant.ofEpochMilli(headers.getExpires()).atOffset(ZoneOffset.UTC).toLocalDateTime());
                }
            } catch (URISyntaxException e) {
                LOGGER.error("Could not parse Google OpenId jwks uri: {} ! Keeping current !", this.metaData.getJwksUri());
            } catch (InvalidPublicKeyException e) {
                LOGGER.error("Could not parse Google OpenId jwks keys: {} ! Keeping current !", e.getMessage());
            }
        }

    }
}
