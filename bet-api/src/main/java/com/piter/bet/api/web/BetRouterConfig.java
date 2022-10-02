package com.piter.bet.api.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BetRouterConfig {

  private static final String BET_URL = "/bets";
  private static final String BET_ID_URL = "/bets/{id}";

  @Bean
  public RouterFunction<ServerResponse> betRoutes(BetWebHandler betWebHandler) {
    return RouterFunctions.route()
        .GET(BET_URL, accept(APPLICATION_JSON), betWebHandler::findAll)
        .GET(BET_ID_URL, accept(APPLICATION_JSON), betWebHandler::findById)
        .POST(BET_URL, accept(APPLICATION_JSON), betWebHandler::saveBet)
        .DELETE(BET_ID_URL, betWebHandler::deleteBet)
        .build();
  }
}
