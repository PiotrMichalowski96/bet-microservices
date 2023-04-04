package com.piter.match.api.config;

import com.piter.security.commons.KeycloakReactiveTokenInstrospector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

  private static final String BET_ADMIN = "BET_ADMIN";

  private static final String MATCHES_ENDPOINT = "/matches/**";
  private static final String ACTUATOR_ENDPOINT = "/actuator/**";
  private static final String SWAGGER_ENDPOINT = "/swagger-doc/**";

  @Bean
  @Profile("!INT-TEST")
  public ReactiveOpaqueTokenIntrospector keycloakIntrospector(OAuth2ResourceServerProperties props) {

    var delegate = new NimbusReactiveOpaqueTokenIntrospector(
        props.getOpaquetoken().getIntrospectionUri(),
        props.getOpaquetoken().getClientId(),
        props.getOpaquetoken().getClientSecret());

    return new KeycloakReactiveTokenInstrospector(delegate);
  }

  @Bean
  @ConditionalOnProperty(prefix = "security", name = "mode", havingValue = "true", matchIfMissing = true)
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveOpaqueTokenIntrospector introspector) {
    return http
        .authorizeExchange()
        .pathMatchers(HttpMethod.POST, MATCHES_ENDPOINT).hasAuthority(BET_ADMIN)
        .pathMatchers(HttpMethod.DELETE, MATCHES_ENDPOINT).hasAuthority(BET_ADMIN)
        .anyExchange().permitAll()
        .and()
        .oauth2ResourceServer()
        .opaqueToken(opaqueToken -> opaqueToken.introspector(introspector))
        .and()
        .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "security", name = "mode", havingValue = "false")
  public SecurityWebFilterChain noSecurityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf().disable()
        .authorizeExchange()
        .pathMatchers(MATCHES_ENDPOINT, ACTUATOR_ENDPOINT, SWAGGER_ENDPOINT).permitAll()
        .and().build();
  }
}
