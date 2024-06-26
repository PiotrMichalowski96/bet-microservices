package com.piter.match.api.it.stepdefs;

import static com.piter.api.commons.util.JsonUtil.convertJson;
import static com.piter.api.commons.util.JsonUtil.convertJsonArray;
import static com.piter.api.commons.util.JsonUtil.readFileAsString;
import static com.piter.api.commons.util.JsonUtil.readJsonArrayFile;
import static com.piter.api.commons.util.JsonUtil.readJsonFile;
import static com.piter.match.api.it.util.AwaitilityUtil.assertAsync;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.it.testcontainers.EnableTestcontainers;
import com.piter.match.api.repository.MatchRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("INT-TEST")
@EnableTestcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties="spring.autoconfigure.exclude=de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration")
@CucumberContextConfiguration
public class MatchApiStepDefs {

  private static final String MATCHES_ENDPOINT = "/matches";
  private static final String MATCH_ENDPOINT = "/matches/{id}";

  private static final String SAMPLES_DIRECTORY = "src/int-test/resources/samples/";
  private static final String INIT_DB_SAMPLE = "fill_out_database.json";

  @LocalServerPort
  private int port;

  @Autowired
  private MatchRepository matchRepository;

  //To keep response state between steps
  private Response response;

  @Before("@InitDb")
  public void initDatabase() throws IOException {
    matchRepository.deleteAll().block();
    List<Match> initialMatches = readJsonArrayFile(SAMPLES_DIRECTORY + INIT_DB_SAMPLE, Match.class);
    initialMatches.forEach(match -> matchRepository.save(match).block());
  }

  @Before
  public void initRestCaller() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  @Given("match {string} is saved in database")
  public void saveMatchInDb(String matchPath) throws IOException {
    Match match = readJsonFile(SAMPLES_DIRECTORY + matchPath, Match.class);
    matchRepository.save(match).block();
  }

  @Given("match {string} does not exist in service")
  public void matchDoesNotExistInService(String matchPath) throws IOException {
    boolean matchExists = doesMatchExistInDatabase(matchPath);
    if (matchExists) {
      throw new IllegalStateException("Match exists in database");
    }
  }

  @Given("match {string} exists in service")
  public void matchExistsInService(String matchPath) throws IOException {
    boolean matchExists = doesMatchExistInDatabase(matchPath);
    if (!matchExists) {
      throw new IllegalStateException("Match does not exist in database");
    }
  }

  private boolean doesMatchExistInDatabase(String matchPath) throws IOException {
    Match match = readJsonFile(SAMPLES_DIRECTORY + matchPath, Match.class);
    if (match.id() == null) {
      return matchRepository.findAll()
          .toStream()
          .anyMatch(m -> isSameMatchCompareByTeams(match, m));
    } else {
      return matchRepository.findById(match.id())
          .blockOptional()
          .isPresent();
    }
  }

  private boolean isSameMatchCompareByTeams(Match m1, Match m2) {
    return m1.homeTeam().equals(m2.homeTeam()) && m1.awayTeam().equals(m2.awayTeam());
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

  @When("user sends post request with match {string}")
  public void saveMatch(String matchPath) throws IOException {
    var matchJson = readFileAsString(SAMPLES_DIRECTORY + matchPath);
    response = given()
        .body(matchJson)
        .contentType(ContentType.JSON)
        .when()
        .post(MATCHES_ENDPOINT);
  }

  @When("user sends request to delete match with ID {int}")
  public void deleteMatch(int id) {
    response = given()
        .pathParams("id", id)
        .when()
        .delete(MATCH_ENDPOINT);
  }

  @Then("match service return response with status {int}")
  public void serviceReturnResponse(Integer expectedStatus) {
    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
  }

  @Then("returned matches are equal to expected {string}")
  public void matchesAreEqualToExpected(String expectedMatchesPath) throws IOException {
    List<Match> expectedMatches = readJsonArrayFile(SAMPLES_DIRECTORY + expectedMatchesPath, Match.class);
    var matchesJson = response.getBody().asString();
    List<Match> actualMatches = convertJsonArray(matchesJson, Match.class);
    assertThat(actualMatches).hasSameSizeAs(expectedMatches);
    assertThat(actualMatches).hasSameElementsAs(expectedMatches);
  }

  @Then("returned match is equal to expected {string}")
  public void matchIsEqualToExpected(String expectedMatchPath) throws IOException {
    Match expectedMatch = readJsonFile(SAMPLES_DIRECTORY + expectedMatchPath, Match.class);
    var matchJson = response.getBody().asString();
    Match actualMatch = convertJson(matchJson, Match.class);
    assertThat(actualMatch).isEqualTo(expectedMatch);
  }

  @Then("match {string} is not present in service")
  public void matchIsNotPresentInDb(String matchPath) throws IOException {
    Match match = readJsonFile(SAMPLES_DIRECTORY + matchPath, Match.class);
    assertAsync(() -> assertMatchIsNotPresentInDb(match));
  }

  private void assertMatchIsNotPresentInDb(Match match) {
    Match foundMatch = findMatchInDb(match);
    assertThat(foundMatch).isNull();
  }

  private Match findMatchInDb(Match searchMatch) {
    return Optional.ofNullable(searchMatch.id())
        .map(id -> matchRepository.findById(id).block())
        .orElse(matchRepository.findAll()
            .filter(m -> isSameMatchCompareByTeams(searchMatch, m))
            .blockFirst());
  }

  @Then("match {string} is saved and retrieved in service")
  public void matchIsSavedAndExpected(String matchPath) throws IOException {
    Match match = readJsonFile(SAMPLES_DIRECTORY + matchPath, Match.class);
    assertAsync(() -> assertSavedMatchIsEqualTo(match));
  }

  private void assertSavedMatchIsEqualTo(Match match) {
    Match savedMatch = findMatchInDb(match);
    assertThat(savedMatch).usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(match);
  }
}
