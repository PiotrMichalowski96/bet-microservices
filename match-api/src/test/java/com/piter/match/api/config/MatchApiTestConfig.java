package com.piter.match.api.config;

import com.piter.match.api.domain.Match;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.producer.MatchProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(value = "com.piter.match.api", excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MatchKafkaProducer.class)
})
public class MatchApiTestConfig {

  //TODO: check if it could be fixed in better way
  //Empty implementation because we don't use it in tests
  @Bean
  @Primary
  MatchProducer matchProducer() {
    return new MatchProducer() {
      @Override
      public Match sendSaveMatchEvent(Match match) {
        return null;
      }

      @Override
      public void sendDeleteMatchEvent(Match match) {

      }
    };
  }
}
