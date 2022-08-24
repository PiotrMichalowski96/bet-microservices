package com.piter.match.service.config;

import java.time.format.DateTimeFormatter;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

@Configuration
public class FeignDateFormatConfig {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  @Bean
  public FeignFormatterRegistrar feignFormatterRegistrar() {
    return registry -> {
      DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
      registrar.setDateTimeFormatter(DATE_TIME_FORMATTER);
      registrar.registerFormatters(registry);
    };
  }
}
