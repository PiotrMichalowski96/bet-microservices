package com.piter.match.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

//TODO: authentication
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

  @Bean
  @ConditionalOnProperty(prefix = "security", name = "mode", havingValue = "true", matchIfMissing = true)
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.authorizeExchange()
        .pathMatchers("/actuator/**", "/match/**", "/swagger-doc/**").permitAll()
        .anyExchange().authenticated()
        .and().build();
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
        .pathMatchers("/actuator/**", "/match/**", "/swagger-doc/**").permitAll()
        .and().build();
  }
}
