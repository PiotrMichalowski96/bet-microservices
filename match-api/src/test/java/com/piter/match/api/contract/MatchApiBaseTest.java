package com.piter.match.api.contract;

import com.piter.api.commons.domain.Match;
import com.piter.api.commons.util.JsonUtil;
import com.piter.match.api.repository.MatchRepository;
import com.piter.match.api.util.DbPopulatorUtil;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("TEST")
@MatchContractTest
public abstract class MatchApiBaseTest {

  private static final String DB_SAMPLES_PATH = "src/test/resources/contracts/db-matches.json";

  @MockBean
  private StreamBridge streamBridge;

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private ApplicationContext context;

  @BeforeEach
  void init() throws IOException {
    initDatabase();
    RestAssuredWebTestClient.applicationContextSetup(context);
  }

  private void initDatabase() throws IOException {
    List<Match> matches = JsonUtil.readJsonArrayFile(DB_SAMPLES_PATH, Match.class);
    DbPopulatorUtil.fillDatabaseIfEmpty(matchRepository, matches);
  }
}
