package com.piter.match.management.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Getter
@Setter
@AllArgsConstructor
public class MatchApiMock {

  private static final String GET_MATCH_BY_ID = "/match/1";
  private static final String GET_MATCHES = "/match?order=match-time";

  private final WireMockServer mockServer;
  private String matchResponse;
  private String matchesResponse;

  public void mockMatchEndpoint() {
    setupMock(GET_MATCH_BY_ID, matchResponse);
  }

  public void mockMatchesEndpoint() {
    setupMock(GET_MATCHES, matchesResponse);
  }

  private void setupMock(String url, String responseBody) {
    mockServer.stubFor(WireMock.get(WireMock.urlEqualTo(url))
        .willReturn(
            WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody(responseBody))
    );
  }
}
