package com.piter.bet.api.config;

import com.piter.security.commons.KeycloakReactiveTokenInstrospector;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
class SecurityConfig {

  private static final String BET_USER = "BET_USER";

  private static final String BETS_ENDPOINT = "/bets/**";
  private static final String CURRENT_USER_ENDPOINT = "/users/current";
  private static final String USERS_RESULT_ENDPOINT = "/users-results/**";

  @Bean
  ReactiveOpaqueTokenIntrospector keycloakIntrospector(OAuth2ResourceServerProperties props) {

    var delegate = new NimbusReactiveOpaqueTokenIntrospector(
        props.getOpaquetoken().getIntrospectionUri(),
        props.getOpaquetoken().getClientId(),
        props.getOpaquetoken().getClientSecret());

    return new KeycloakReactiveTokenInstrospector(delegate);
  }

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveOpaqueTokenIntrospector introspector) {
    return http
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers(HttpMethod.GET, BETS_ENDPOINT).hasAuthority(BET_USER)
            .pathMatchers(HttpMethod.POST, BETS_ENDPOINT).hasAuthority(BET_USER)
            .pathMatchers(HttpMethod.DELETE, BETS_ENDPOINT).hasAuthority(BET_USER)
            .pathMatchers(HttpMethod.GET, CURRENT_USER_ENDPOINT).hasAuthority(BET_USER)
            .pathMatchers(HttpMethod.GET, USERS_RESULT_ENDPOINT).permitAll()
            .anyExchange().permitAll()
        )
        .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .opaqueToken(opaqueToken -> opaqueToken.introspector(introspector))
        )
        .build();
  }
}
