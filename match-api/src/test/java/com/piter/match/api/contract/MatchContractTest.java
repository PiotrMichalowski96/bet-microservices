package com.piter.match.api.contract;

import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.config.SecurityTestConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import({MatchApiTestConfig.class, SecurityTestConfig.class})
@ImportAutoConfiguration(exclude = ContextFunctionCatalogAutoConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public @interface MatchContractTest {
}
