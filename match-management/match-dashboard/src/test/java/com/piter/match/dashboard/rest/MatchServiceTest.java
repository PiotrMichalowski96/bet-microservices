package com.piter.match.dashboard.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.piter.match.dashboard.config.WireMockConfig;
import com.piter.match.dashboard.mock.MatchApiMock;
import com.piter.match.dashboard.util.JsonUtil;
import com.piter.match.service.config.MatchFeignConfig;
import com.piter.match.service.domain.Match;
import com.piter.match.service.rest.HandledMatchService;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("TEST")
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MatchFeignConfig.class, WireMockConfig.class})
class MatchServiceTest {

  private static final String MATCH_JSON_PATH = "samples/match.json";
  private static final String MATCHES_JSON_PATH = "samples/matches.json";

  @Autowired
  private WireMockServer wireMockServer;

  @Autowired
  private HandledMatchService handledMatchService;

  @BeforeEach
  void init() throws IOException {
    String matchJson = JsonUtil.readFileAsString(MATCH_JSON_PATH);
    String matchesJson = JsonUtil.readFileAsString(MATCHES_JSON_PATH);

    MatchApiMock matchApiMock = new MatchApiMock(wireMockServer, matchJson, matchesJson);
    matchApiMock.mockMatchEndpoint();
    matchApiMock.mockMatchesEndpoint();
  }

  @Test
  void shouldGetMatchById() throws IOException {
    //given
    Long id = 1L;
    Match expectedMatch = JsonUtil.readJsonFile(MATCH_JSON_PATH, Match.class);

    //when
    Match actualMatch = handledMatchService.getMatch(id).get();

    //then
    assertThat(actualMatch).isEqualTo(expectedMatch);
  }

  @Test
  void shouldGetMatches() throws IOException {
    //given
    List<Match> expectedMatches = JsonUtil.readJsonArrayFile(MATCHES_JSON_PATH, Match.class);
    String order = "match-time";

    //when
    List<Match> matches = handledMatchService.getMatchList(order);

    //then
    assertThat(matches).hasSameElementsAs(expectedMatches);
  }
}