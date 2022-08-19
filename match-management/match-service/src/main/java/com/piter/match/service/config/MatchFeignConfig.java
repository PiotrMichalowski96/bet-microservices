package com.piter.match.service.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableFeignClients(basePackages = "com.piter.match.service.rest")
@Import({CacheConfig.class, FeignDateFormatConfig.class})
@PropertySource(value = "classpath:default-feign-match.yaml", factory = YamlPropertySourceFactory.class)
@ComponentScan(basePackages = "com.piter.match.service.rest")
public class MatchFeignConfig {

}
