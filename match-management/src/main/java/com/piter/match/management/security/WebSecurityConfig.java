package com.piter.match.management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

  @Bean
  public DefaultSecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeRequests()
        .anyRequest().permitAll()
        .and()
        .oauth2Login()
        .defaultSuccessUrl("/dashboard/match")
        .and()
        .build();
  }
}
