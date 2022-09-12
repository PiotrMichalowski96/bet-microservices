package com.piter.match.api.it.docker;

import java.io.File;
import java.time.Duration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties="spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
@ActiveProfiles("INT-TEST")
@Testcontainers
@ContextConfiguration(initializers = DockerContextInitializer.class)
public abstract class AbstractDockerIntegrationTest {

  private static final File COMPOSE_FILE = new File("src/int-test/resources/docker/docker-compose.yaml");
  private static final String ZOOKEEPER_SERVICE = "zookeeper";
  private static final String BROKER_SERVICE = "broker";
  private static final String MONGODB_SERVICE = "mongodb";

  @Container
  public static DockerComposeContainer<?> dockerComposeContainer =
      new DockerComposeContainer<>(COMPOSE_FILE)
          .withServices(ZOOKEEPER_SERVICE, BROKER_SERVICE, MONGODB_SERVICE)
          .withExposedService(BROKER_SERVICE, 29092, Wait.forListeningPort()
              .withStartupTimeout(Duration.ofMinutes(2)))
          .withExposedService(MONGODB_SERVICE, 27017, Wait.forListeningPort()
              .withStartupTimeout(Duration.ofMinutes(2)))
          .withLocalCompose(true);
}
