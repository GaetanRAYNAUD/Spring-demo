package com.example.demo.config;

import com.example.demo.config.filter.AuthenticationFilter;
import com.example.demo.config.filter.AuthorizationFilter;
import com.example.demo.config.properties.DemoProperties;
import com.example.demo.service.JwtService;
import com.example.demo.service.definition.ResetKeyService;
import com.example.demo.service.google.RecaptchaV3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    private final UserDetailsService userService;

    private final ResetKeyService resetKeyService;

    private final DemoProperties demoProperties;

    private final RecaptchaV3Service recaptchaV3Service;

    public WebSecurityConfig(JwtService jwtService, ObjectMapper objectMapper, UserDetailsService userService, ResetKeyService resetKeyService,
                             DemoProperties demoProperties, RecaptchaV3Service recaptchaV3Service) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.resetKeyService = resetKeyService;
        this.demoProperties = demoProperties;
        this.recaptchaV3Service = recaptchaV3Service;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), this.jwtService, this.objectMapper, this.resetKeyService,
                                                                             this.recaptchaV3Service);
        authenticationFilter.setFilterProcessesUrl("/public/authentication/login");

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();
        http.authorizeRequests().antMatchers("/public/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(authenticationFilter);
        http.addFilter(new AuthorizationFilter(authenticationManager(), this.jwtService, this.objectMapper));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //Allow swagger and public urls to be accessed without authentication
        web.ignoring().antMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**",
                                   "/gs-guide-websocket", "/favicon.*");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedOrigins(this.demoProperties.getCorsOrigin());
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
