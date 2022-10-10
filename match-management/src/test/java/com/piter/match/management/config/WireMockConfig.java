package com.piter.match.management.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {

  @Value("${match-api.port:9651}")
  private int port;

  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer mockMatchServer() {
    return new WireMockServer(port);
  }
}
