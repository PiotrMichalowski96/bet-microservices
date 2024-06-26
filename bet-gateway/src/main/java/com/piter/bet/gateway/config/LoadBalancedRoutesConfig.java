package com.piter.bet.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadBalancedRoutesConfig {

  @Bean
  RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder){
    return builder.routes()
        .route(r -> r.path("/matches/**")
            .uri("lb://match-api"))
        .route(r -> r.path("/bets/**", "/users-results/**", "/users/current")
            .uri("lb://bet-api"))
        .build();
  }
}
