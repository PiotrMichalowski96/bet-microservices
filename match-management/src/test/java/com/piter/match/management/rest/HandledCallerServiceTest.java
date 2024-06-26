package com.piter.match.management.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;

import com.piter.match.management.config.WireMockConfig;
import com.piter.match.management.domain.Match;
import com.piter.match.management.mock.MatchApiMock;
import com.piter.match.management.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//TODO: fix wiremock
@Disabled
@SpringBootTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WireMockConfig.class)
class HandledCallerServiceTest {

  private static final String MATCH_JSON_PATH = "samples/match.json";
  private static final String MATCHES_JSON_PATH = "samples/matches.json";

  @Autowired
  private WireMockServer wireMockServer;

  @Autowired
  private HandledCallerService handledCallerService;

  @BeforeEach
  void init() throws IOException {
    var matchJson = JsonUtil.readFileAsString(MATCH_JSON_PATH);
    var matchesJson = JsonUtil.readFileAsString(MATCHES_JSON_PATH);

    var matchApiMock = new MatchApiMock(wireMockServer, matchJson, matchesJson);
    matchApiMock.mockMatchEndpoint();
    matchApiMock.mockMatchesEndpoint();
  }

  @Test
  void shouldGetMatchById() throws IOException {
    //given
    var id = 1L;
    Match expectedMatch = JsonUtil.readJsonFile(MATCH_JSON_PATH, Match.class);

    //when
    Match actualMatch = handledCallerService.getMatch(id).get();

    //then
    assertThat(actualMatch).isEqualTo(expectedMatch);
  }

  @Test
  void shouldGetMatches() throws IOException {
    //given
    List<Match> expectedMatches = JsonUtil.readJsonArrayFile(MATCHES_JSON_PATH, Match.class);
    var order = "match-time";

    //when
    List<Match> matches = handledCallerService.getMatchList(order);

    //then
    assertThat(matches).hasSameElementsAs(expectedMatches);
  }
}