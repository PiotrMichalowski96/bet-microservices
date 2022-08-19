package com.piter.match.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:default-kafka-match.yaml", factory = YamlPropertySourceFactory.class)
@ComponentScan(basePackages = "com.piter.match.service.producer")
public class MatchKafkaConfig {

}
