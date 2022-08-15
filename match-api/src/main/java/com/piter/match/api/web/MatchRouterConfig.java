package com.piter.match.api.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MatchRouterConfig {

  private static final String MATCH_URL = "/match";
  private static final String MATCH_ID_URL = "/match/{id}";

  private static final String ORDER_BY_PARAM = "order";
  private static final String MATCH_TIME_PARAM_VALUE = "match-time";
  private static final String ROUND_TIME_PARAM_VALUE = "round-time";

  @Bean
  public RouterFunction<ServerResponse> matchRoutes(MatchWebHandler matchWebHandler) {
    return RouterFunctions.route()
        .GET(MATCH_URL, queryParam(ORDER_BY_PARAM, MATCH_TIME_PARAM_VALUE::equals), matchWebHandler::findAllByOrderByMatchStartTime)
        .GET(MATCH_URL, queryParam(ORDER_BY_PARAM, ROUND_TIME_PARAM_VALUE::equals), matchWebHandler::findAllByOrderByMatchRoundStartTime)
        .GET(MATCH_URL, accept(APPLICATION_JSON), matchWebHandler::findAll)
        .GET(MATCH_ID_URL, accept(APPLICATION_JSON), matchWebHandler::findById)
        .build();
  }
}
