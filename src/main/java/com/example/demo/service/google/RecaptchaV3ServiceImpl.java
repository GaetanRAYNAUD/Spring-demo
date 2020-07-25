package com.example.demo.service.google;

import com.example.demo.common.Constants;
import com.example.demo.common.exception.RecaptchaV3Exception;
import com.example.demo.config.properties.DemoProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class RecaptchaV3ServiceImpl implements RecaptchaV3Service {

    private final DemoProperties demoProperties;

    private final List<String> acceptedHosts;

    public RecaptchaV3ServiceImpl(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
        this.acceptedHosts = this.demoProperties.getCorsOrigin()
                                                .stream()
                                                .flatMap(s -> {
                                                    try {
                                                        return Stream.of(new URL(s));
                                                    } catch (MalformedURLException e) {
                                                        return Stream.empty();
                                                    }
                                                })
                                                .map(URL::getHost)
                                                .collect(Collectors.toList());
    }

    @Override
    public void validateToken(String token, String user, RecaptchaV3Action action) {
        if (!this.demoProperties.getRecaptcha().isEnable()) {
            return;
        }

        if (StringUtils.isBlank(token)) {
            throw new BadCredentialsException("Someone tried to " + action.name() + " with an empty recaptchaV3 token for user: " + user + " !");
        }

        try {
            String verificationUrl = UriComponentsBuilder.fromHttpUrl(Constants.RECAPTCHA_URL)
                                                         .queryParam("secret", this.demoProperties.getRecaptcha().getKey())
                                                         .queryParam("response", token)
                                                         .toUriString();
            ResponseEntity<RecaptchaV3VerificationResponseBody> response = new RestTemplate()
                    .postForEntity(verificationUrl, null, RecaptchaV3VerificationResponseBody.class);

            RecaptchaV3VerificationResponseBody body = response.getBody();

            if (body == null) {
                throw new RecaptchaV3Exception("RecaptchaV3 validation request returned empty body !");
            }

            if (body.getErrorCodes() != null && !body.getErrorCodes().isEmpty()) {
                throw new RecaptchaV3Exception("Something occurred when trying to validate a recaptchaV3 token: " + String.join("; ", body.getErrorCodes()));
            }

            if (!body.isSuccess()) {
                throw new BadCredentialsException("Someone tried to " + action.name() + " for user: " + user + " and failed ! Errors: "
                                                  + String.join("; ", body.getErrorCodes()));
            }

            if (!body.getAction().equals(action.name())) {
                throw new BadCredentialsException("Someone tried to " + action.name() + " for user: " + user + " and with the wrong action. Expected: "
                                                  + action.name() + ", got: " + body.getAction());
            }

            if (!this.acceptedHosts.contains(body.getHostname())) {
                throw new BadCredentialsException("Someone tried to " + action.name() + " for user: " + user + " and with a wrong hostname: "
                                                  + body.getHostname());
            }

            if (body.getScore() < this.demoProperties.getRecaptcha().getThreshold()) {
                throw new BadCredentialsException("Did a robot just tried to " + action.name() + " for user: " + user + " ? (Score: " + body.getScore() + ")");
            }
        } catch (RestClientException e) {
            throw new RecaptchaV3Exception("Something occurred when trying to validate a recaptchaV3 token: " + e.getMessage());
        }
    }
}
