package com.piter.match.dashboard;

import com.piter.match.service.config.MatchFeignConfig;
import com.piter.match.service.config.MatchKafkaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({MatchFeignConfig.class, MatchKafkaConfig.class})
@SpringBootApplication
public class MatchDashboardApplication {

  public static void main(String[] args) {
    SpringApplication.run(MatchDashboardApplication.class, args);
  }
}
