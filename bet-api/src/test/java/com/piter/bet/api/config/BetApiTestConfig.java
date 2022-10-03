package com.piter.bet.api.config;

import static org.mockito.Mockito.mock;

import com.piter.bet.api.consumer.BetKafkaConsumer;
import com.piter.bet.api.producer.BetKafkaProducer;
import com.piter.bet.api.service.BetService;
import com.piter.bet.api.web.BetController;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.Validator;

@TestConfiguration
@Import({BetKafkaConsumer.class,
    BetService.class,
    BetController.class
})
public class BetApiTestConfig {

  //Mock because we don't use it in tests - just avoid missing bean errors
  @Bean
  @Primary
  BetKafkaProducer matchProducer() {
    return mock(BetKafkaProducer.class);
  }

  @Bean
  @Primary
  Validator validator() {
    return mock(Validator.class);
  }
}
