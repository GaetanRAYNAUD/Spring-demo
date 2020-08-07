package com.example.demo.service;

import com.example.demo.common.Constants;
import com.example.demo.config.properties.DemoProperties;
import com.example.demo.model.User;
import com.example.demo.service.google.GoogleOpenIdService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtParserBuilder;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtServiceImpl implements JwtService {

    protected final SessionService sessionService;

    protected final GoogleOpenIdService googleOpenIdService;

    protected final JwtParserBuilder jwtParserBuilder;

    protected final DemoProperties demoProperties;

    protected final PublicKey publicKey;

    protected final SecretKey secretKey;

    public JwtServiceImpl(SessionService sessionService, GoogleOpenIdService googleOpenIdService,
                          DemoProperties demoProperties) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.sessionService = sessionService;
        this.googleOpenIdService = googleOpenIdService;
        this.demoProperties = demoProperties;
        this.jwtParserBuilder = new DefaultJwtParserBuilder();
        this.publicKey = KeyFactory.getInstance("RSA")
                                   .generatePublic(new X509EncodedKeySpec(Base64.getDecoder()
                                                                                .decode(this.demoProperties.getSecurityJwt()
                                                                                                           .getPublicKey()
                                                                                                           .getBytes())));
        this.secretKey = Keys.hmacShaKeyFor(this.demoProperties.getSecurityJwt().getSecretKey().getBytes());
    }

    @Override
    public void authenticateFromRequest(HttpServletRequest request) {
        final Optional<String> token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (token.isPresent() && token.get().startsWith(Constants.BEARER) &&
            token.get().length() > Constants.BEARER.length()) {
            this.sessionService.authenticateFromToken(getClaims(token.get()));
        } else {
            throw new AuthenticationCredentialsNotFoundException("Missing authorization !");
        }
    }

    @Override
    public Pair<String, Date> generateToken(User user) {
        Date now = new Date();
        Date expirationDate = DateUtils.addMinutes(now, this.demoProperties.getSecurityJwt().getExpiration());

        JwtBuilder builder = Jwts.builder()
                                 .setSubject(user.getEmail())
                                 .setIssuedAt(now)
                                 .setExpiration(expirationDate)
                                 .setIssuer(this.demoProperties.getSecurityJwt().getIssuer())
                                 .claim(Constants.ROLES_CLAIM, user.getAuthorities())
                                 .signWith(this.secretKey, SignatureAlgorithm.HS256);
        return new ImmutablePair<>(builder.compact(), expirationDate);
    }

    @Override
    public Claims getClaims(String bearerToken) {
        if (bearerToken.startsWith(Constants.BEARER)) {
            bearerToken = bearerToken.substring(Constants.BEARER.length() + 1);
        }

        return Jwts.parserBuilder()
                   .requireIssuer(this.demoProperties.getSecurityJwt().getIssuer())
                   .setSigningKey(this.secretKey)
                   .build()
                   .parseClaimsJws(bearerToken)
                   .getBody();
    }

    @Override
    public Claims getGoogleClaims(String token) {
        return this.googleOpenIdService.validateToken(token);
    }
}
