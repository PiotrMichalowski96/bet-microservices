package com.piter.match.api.config;

import com.piter.match.api.producer.MatchProducer;
import com.piter.match.api.service.MatchService;
import com.piter.match.api.web.MatchRouterConfig;
import com.piter.match.api.web.MatchWebHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(value = "com.piter.match.api", excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MatchProducer.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MatchService.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MatchWebHandler.class),
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MatchRouterConfig.class)
})
public class MongoTestConfig {

}
