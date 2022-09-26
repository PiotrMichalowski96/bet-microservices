package com.piter.bet.event.aggregator.prediction.config;

import com.piter.bet.event.aggregator.prediction.expression.ExpressionPrediction;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "bet")
public class PredictionProperties {

  private List<ExpressionPrediction> rules;
}
