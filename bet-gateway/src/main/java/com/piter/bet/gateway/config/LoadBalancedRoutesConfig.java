package com.piter.bet.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery")
@Configuration
public class LoadBalancedRoutesConfig {

  @Bean
  public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder){
    return builder.routes()
        .route(r -> r.path("/matches", "/matches/*")
            .uri("lb://match-api"))
        .route(r -> r.path("/bets", "/bets/*", "/bets/results/users", "/bets/results/users/*", "/users/current")
            .uri("lb://bet-api"))
        .build();
  }
}
