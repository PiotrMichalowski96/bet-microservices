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
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
class SecurityConfig {

  private static final String BET_ADMIN = "BET_ADMIN";

  private static final String MATCHES_ENDPOINT = "/api/v2/matches/**";
  private static final String ACTUATOR_ENDPOINT = "/actuator/**";
  private static final String SWAGGER_ENDPOINT = "/swagger-doc/**";

  @Bean
  @Profile("!INT-TEST")
  ReactiveOpaqueTokenIntrospector keycloakIntrospector(OAuth2ResourceServerProperties props) {

    var delegate = new NimbusReactiveOpaqueTokenIntrospector(
        props.getOpaquetoken().getIntrospectionUri(),
        props.getOpaquetoken().getClientId(),
        props.getOpaquetoken().getClientSecret());

    return new KeycloakReactiveTokenInstrospector(delegate);
  }

  @Bean
  @ConditionalOnProperty(prefix = "security", name = "mode", havingValue = "true", matchIfMissing = true)
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
      ReactiveOpaqueTokenIntrospector introspector) {
    return http
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers(HttpMethod.POST, MATCHES_ENDPOINT).hasAuthority(BET_ADMIN)
            .pathMatchers(HttpMethod.DELETE, MATCHES_ENDPOINT).hasAuthority(BET_ADMIN)
            .anyExchange().permitAll()
        )
        .oauth2ResourceServer(oauth2Server -> oauth2Server
            .opaqueToken(opaqueToken -> opaqueToken.introspector(introspector))
        )
        .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "security", name = "mode", havingValue = "false")
  SecurityWebFilterChain noSecurityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(CsrfSpec::disable)
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers(MATCHES_ENDPOINT, ACTUATOR_ENDPOINT, SWAGGER_ENDPOINT).permitAll()
        )
        .build();
  }
}
