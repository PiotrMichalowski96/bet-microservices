package com.piter.match.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
public class SecurityConfig {

  private static final String BET_USER = "BET_USER";
  private static final String BET_ADMIN = "BET_ADMIN";

  @Bean
  public ReactiveOpaqueTokenIntrospector keycloakIntrospector(OAuth2ResourceServerProperties props) {

    NimbusReactiveOpaqueTokenIntrospector delegate = new NimbusReactiveOpaqueTokenIntrospector(
        props.getOpaquetoken().getIntrospectionUri(),
        props.getOpaquetoken().getClientId(),
        props.getOpaquetoken().getClientSecret());

    return new KeycloakReactiveTokenInstrospector(delegate);
  }

  @Bean
  @ConditionalOnProperty(prefix = "security", name = "mode", havingValue = "true", matchIfMissing = true)
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveOpaqueTokenIntrospector introspector) {
    http.authorizeExchange(exchange -> exchange
            .pathMatchers(HttpMethod.GET, "/matches/**").hasAuthority(BET_USER)
            .pathMatchers(HttpMethod.POST, "/matches/**").hasAuthority(BET_ADMIN)
            .pathMatchers(HttpMethod.DELETE, "/matches/**").hasAuthority(BET_ADMIN)
            .anyExchange()
            .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .opaqueToken(opaqueToken -> opaqueToken.introspector(introspector)));
    return http.build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "security", name = "mode", havingValue = "false")
  public SecurityWebFilterChain noSecurityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf().disable()
        .cors().disable()
        .headers().frameOptions().disable()
        .and()
        .authorizeExchange()
        .pathMatchers("/actuator/**", "/matches/**", "/swagger-doc/**").permitAll()
        .and().build();
  }
}
