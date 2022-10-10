package com.piter.match.api.config;

import static org.mockito.Mockito.mock;

import com.piter.match.api.consumer.MatchKafkaConsumer;
import com.piter.match.api.producer.MatchProducer;
import com.piter.match.api.service.MatchService;
import com.piter.match.api.service.SequenceGeneratorService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.Validator;

@TestConfiguration
@Import({MatchKafkaConsumer.class,
    MatchService.class,
    SequenceGeneratorService.class
})
public class MatchApiTestConfig {

  //Mock because we don't use it in tests - just avoid missing bean errors
  @Bean
  @Primary
  MatchProducer matchProducer() {
    return mock(MatchProducer.class);
  }

  @Bean
  @Primary
  Validator validator() {
    return mock(Validator.class);
  }
}
