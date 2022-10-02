package com.piter.match.api.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;

import com.piter.match.api.domain.Match;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Match API", version = "1.0", description = "Documentation for Match API"))
public class MatchRouterConfig {

  private static final String MATCH_URL = "/match";
  private static final String MATCH_ID_URL = "/match/{id}";

  private static final String ORDER_BY_PARAM = "order";
  private static final String MATCH_TIME_PARAM_VALUE = "match-time";
  private static final String ROUND_TIME_PARAM_VALUE = "round-time";

  @Bean
  @RouterOperations(
      {
          @RouterOperation(path = MATCH_URL,
              produces = APPLICATION_JSON_VALUE,
              method = RequestMethod.GET,
              operation = @Operation(
                  operationId = "getMatches",
                  responses = {
                      @ApiResponse(responseCode = "200", description = "successful found match list", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Match.class)))),
                      @ApiResponse(responseCode = "404", description = "matches not found")},
                  parameters = {
                      @Parameter(in = ParameterIn.QUERY, name = "order", example = "match-time, round-time")
                  })),
          @RouterOperation(path = MATCH_ID_URL,
              produces = APPLICATION_JSON_VALUE,
              method = RequestMethod.GET,
              operation = @Operation(
                  operationId = "getMatch",
                  responses = {
                      @ApiResponse(responseCode = "200", description = "successful found match", content = @Content(schema = @Schema(implementation = Match.class))),
                      @ApiResponse(responseCode = "404", description = "match not found")},
                  parameters = {
                      @Parameter(in = ParameterIn.PATH, name = "id", description = "match id", example = "1")
                  }))
      }
  )
  public RouterFunction<ServerResponse> matchRoutes(MatchWebHandler matchWebHandler) {
    return RouterFunctions.route()
        .GET(MATCH_URL, queryParam(ORDER_BY_PARAM, MATCH_TIME_PARAM_VALUE::equals), matchWebHandler::findAllByOrderByMatchStartTime)
        .GET(MATCH_URL, queryParam(ORDER_BY_PARAM, ROUND_TIME_PARAM_VALUE::equals), matchWebHandler::findAllByOrderByMatchRoundStartTime)
        .GET(MATCH_URL, accept(APPLICATION_JSON), matchWebHandler::findAll)
        .GET(MATCH_ID_URL, accept(APPLICATION_JSON), matchWebHandler::findById)
        .POST(MATCH_URL, accept(APPLICATION_JSON), matchWebHandler::saveMatch)
        .DELETE(MATCH_ID_URL, matchWebHandler::deleteMatch)
        .build();
  }
}
