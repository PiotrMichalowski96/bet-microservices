package com.piter.match.api.it.docker;

import static com.piter.match.api.it.docker.AbstractDockerIntegrationTest.dockerComposeContainer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class DockerContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    dockerComposeContainer.start();
  }
}
