package com.piter.bet.api.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.piter.bet.api.consumer.BetKafkaConsumer;
import com.piter.bet.api.producer.BetKafkaProducer;
import com.piter.bet.api.service.BetService;
import com.piter.bet.api.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Opaquetoken;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.Validator;

@TestConfiguration
@Import({BetKafkaConsumer.class,
    BetService.class,
    UserService.class
})
public class BetApiTestConfig {

  //Mock because we don't use it in tests - just avoid missing bean errors
  @Bean
  @Primary
  BetKafkaProducer matchProducer() {
    return mock(BetKafkaProducer.class);
  }

  @Bean
  Validator validator() {
    return mock(Validator.class);
  }

  @Bean
  @Primary
  OAuth2ResourceServerProperties serverProperties() {
    OAuth2ResourceServerProperties mockServerProperties = mock(OAuth2ResourceServerProperties.class);
    Opaquetoken mockOpaqueToken = mock(Opaquetoken.class);
    when(mockServerProperties.getOpaquetoken()).thenReturn(mockOpaqueToken);
    when(mockOpaqueToken.getClientId()).thenReturn(RandomStringUtils.randomAlphabetic(10));
    when(mockOpaqueToken.getIntrospectionUri()).thenReturn(RandomStringUtils.randomAlphabetic(10));
    when(mockOpaqueToken.getClientSecret()).thenReturn(RandomStringUtils.randomAlphabetic(10));
    return mockServerProperties;
  }
}
