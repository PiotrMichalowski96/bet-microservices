package com.piter.bet.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@EnableWebFluxSecurity
@SpringBootApplication
public class BetGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(BetGatewayApplication.class, args);
  }

}
