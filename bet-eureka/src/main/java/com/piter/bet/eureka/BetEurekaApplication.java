package com.piter.bet.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class BetEurekaApplication {

  public static void main(String[] args) {
    SpringApplication.run(BetEurekaApplication.class, args);
  }

}
