package com.piter.match.api.it.stepdefs;

import static com.piter.match.api.it.util.JsonUtil.convertJson;
import static com.piter.match.api.it.util.JsonUtil.convertJsonArray;
import static com.piter.match.api.it.util.JsonUtil.readJsonArrayFile;
import static com.piter.match.api.it.util.JsonUtil.readJsonFile;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.match.api.domain.Match;
import com.piter.match.api.it.docker.AbstractDockerIntegrationTest;
import com.piter.match.api.repository.MatchRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@CucumberContextConfiguration
public class MatchApiStepDefs extends AbstractDockerIntegrationTest {

  private static final String MATCHES_ENDPOINT = "/match";
  private static final String MATCH_ENDPOINT = "/match/{id}";

  private static final String INIT_DB_PATH = "samples/fill_out_database.json";

  private final MatchRepository matchRepository;

  //To keep response state between steps
  private Response response;

  @Before
  public void initDatabase() throws IOException {
    List<Match> matches = matchRepository.findAll()
        .collectList()
        .block();

    if (matches == null || matches.isEmpty()) {
      List<Match> initialMatches = readJsonArrayFile(INIT_DB_PATH, Match.class);
      matchRepository.saveAll(initialMatches).subscribe();
    }
  }

  @Before
  public void initRestCaller() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 7777;
  }

  @When("user sends request to get all matches")
  public void requestAllMatches() {
    response = given()
        .when()
        .get(MATCHES_ENDPOINT);
  }

  @When("user sends request to get matches ordered by {string}")
  public void requestMatchesOrdered(String order) {
    response = given()
        .queryParam("order", order)
        .when()
        .get(MATCHES_ENDPOINT);
  }

  @When("user sends request to find match by ID {int}")
  public void requestMatchById(int id) {
    response = given()
        .pathParam("id", id)
        .when()
        .get(MATCH_ENDPOINT);
  }

  @Then("match service return response with status {int}")
  public void serviceReturnResponse(Integer expectedStatus) {
    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
  }

  @Then("returned matches are equal to expected {string}")
  public void matchesAreEqualToExpected(String expectedMatchesPath) throws IOException {
    List<Match> expectedMatches = readJsonArrayFile(expectedMatchesPath, Match.class);
    var matchesJson = response.getBody().asString();
    List<Match> actualMatches = convertJsonArray(matchesJson, Match.class);
    assertThat(actualMatches).hasSameSizeAs(expectedMatches);
    assertThat(actualMatches).hasSameElementsAs(expectedMatches);
  }

  @Then("returned match is equal to expected {string}")
  public void matchIsEqualToExpected(String expectedMatchPath) throws IOException {
    Match expectedMatch = readJsonFile(expectedMatchPath, Match.class);
    var matchJson = response.getBody().asString();
    Match actualMatch = convertJson(matchJson, Match.class);
    assertThat(actualMatch).isEqualTo(expectedMatch);
  }
}
