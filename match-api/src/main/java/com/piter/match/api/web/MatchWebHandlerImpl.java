package com.piter.match.api.web;

import com.piter.match.api.domain.Match;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MatchWebHandlerImpl implements MatchWebHandler {

  private final MatchService matchService;

  @Override
  public Mono<ServerResponse> findAll(ServerRequest request) {
    return ServerResponse.ok().body(matchService.findAll(), Match.class);
  }

  @Override
  public Mono<ServerResponse> findAllByOrderByMatchStartTime(ServerRequest request) {
    return ServerResponse.ok().body(matchService.findAllByOrderByMatchStartTime(), Match.class);
  }

  @Override
  public Mono<ServerResponse> findAllByOrderByMatchRoundStartTime(ServerRequest request) {
    return ServerResponse.ok().body(matchService.findAllByOrderByMatchRoundStartTime(), Match.class);
  }

  @Override
  public Mono<ServerResponse> findById(ServerRequest request) {
    var id = Long.valueOf(request.pathVariable("id"));
    return matchService.findById(id)
        .flatMap(match -> ServerResponse.ok().bodyValue(match))
        .onErrorResume(e -> e instanceof MatchNotFoundException, e -> ServerResponse.notFound().build());
  }

  @Override
  public Mono<ServerResponse> saveStock(ServerRequest request) {
    return request.bodyToMono(Match.class)
        .flatMap(matchService::saveMatch)
        .flatMap(match -> ServerResponse.ok().bodyValue(match));
  }

  @Override
  public Mono<ServerResponse> deleteStock(ServerRequest request) {
    var id = Long.valueOf(request.pathVariable("id"));
    return matchService.deleteMatch(id)
        .flatMap(voidMono -> ServerResponse.ok().build())
        .onErrorResume(e -> e instanceof MatchNotFoundException, e -> ServerResponse.notFound().build());
  }
}
