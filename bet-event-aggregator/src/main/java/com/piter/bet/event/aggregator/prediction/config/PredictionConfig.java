package com.piter.bet.event.aggregator.prediction.config;

import com.piter.bet.event.aggregator.prediction.BetPredictionFetcher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = PredictionProperties.class)
public class PredictionConfig {

  @Bean
  public BetPredictionFetcher betPredictionFetcher(PredictionProperties predictionProperties) {
    return new BetPredictionFetcher(predictionProperties.getRules());
  }
}
