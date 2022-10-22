package com.piter.bet.api.config;

import com.piter.api.commons.security.KeycloakReactiveTokenInstrospector;
import java.util.List;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

  private static final String BET_USER = "BET_USER";

  @Bean
  public ReactiveOpaqueTokenIntrospector keycloakIntrospector(OAuth2ResourceServerProperties props) {

    var delegate = new NimbusReactiveOpaqueTokenIntrospector(
        props.getOpaquetoken().getIntrospectionUri(),
        props.getOpaquetoken().getClientId(),
        props.getOpaquetoken().getClientSecret());

    return new KeycloakReactiveTokenInstrospector(delegate);
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveOpaqueTokenIntrospector introspector) {
    return http
        .authorizeExchange()
        .pathMatchers(HttpMethod.GET, "/bets/**").hasAuthority(BET_USER)
        .pathMatchers(HttpMethod.POST, "/bets/**").hasAuthority(BET_USER)
        .pathMatchers(HttpMethod.DELETE, "/bets/**").hasAuthority(BET_USER)
        .anyExchange().permitAll()
        .and()
        .oauth2ResourceServer()
        .opaqueToken(opaqueToken -> opaqueToken.introspector(introspector))
        .and()
        .build();
  }

  //TODO: for development of front end Angular app
  @Bean
  public CorsWebFilter corsWebFilter() {
    var corsConfig = new CorsConfiguration();
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedOrigins(List.of("http://localhost:4201"));
    corsConfig.addAllowedMethod("*");
    corsConfig.addAllowedHeader("*");

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }
}
