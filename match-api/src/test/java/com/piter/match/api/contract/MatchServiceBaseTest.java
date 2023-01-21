package com.piter.match.api.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("TEST")
@WithMockUser(authorities = "BET_ADMIN")
@MatchContractTest
public class MatchServiceBaseTest {

  @MockBean
  private StreamBridge streamBridge;

  @Autowired
  private WebApplicationContext context;

  @BeforeEach
  public void setup() {
    RestAssuredMockMvc.webAppContextSetup(context);
  }
}
