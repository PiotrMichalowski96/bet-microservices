package com.piter.match.api;

import com.piter.api.commons.config.EurekaDiscoveryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(EurekaDiscoveryConfig.class)
public class MatchApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(MatchApiApplication.class, args);
  }
}
