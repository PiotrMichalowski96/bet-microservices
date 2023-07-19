package com.piter.match.api.it.testcontainers;

import java.time.Duration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.1.1"))
      .withStartupTimeout(Duration.ofMinutes(2));

  private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.12"))
      .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));

  static {
    Startables.deepStart(kafkaContainer, mongoDBContainer).join();
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    TestPropertyValues.of(
        "spring.data.mongodb.host=" + mongoDBContainer.getHost(),
        "spring.data.mongodb.port=" + mongoDBContainer.getFirstMappedPort(),
        "spring.cloud.stream.kafka.binder.brokers=" + kafkaContainer.getBootstrapServers()
    ).applyTo(applicationContext.getEnvironment());
  }
}
