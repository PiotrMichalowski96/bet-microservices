package com.piter.bet.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(SecurityConfig.class)
public class SecurityTestConfig {

}
