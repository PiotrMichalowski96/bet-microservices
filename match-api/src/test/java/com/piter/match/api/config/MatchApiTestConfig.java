package com.piter.match.api.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.piter.match.api.consumer.MatchKafkaConsumer;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.service.MatchService;
import com.piter.match.api.service.SequenceGeneratorService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Opaquetoken;
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
  MatchKafkaProducer matchProducer() {
    return mock(MatchKafkaProducer.class);
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
