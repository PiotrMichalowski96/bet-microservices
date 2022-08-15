package com.piter.match.api.web;

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
    return matchService.findAll()
        .flatMap(matches -> ServerResponse.ok().bodyValue(matches))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  @Override
  public Mono<ServerResponse> findAllByOrderByMatchStartTime(ServerRequest request) {
    return matchService.findAllByOrderByMatchStartTime()
        .flatMap(matches -> ServerResponse.ok().bodyValue(matches))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  @Override
  public Mono<ServerResponse> findAllByOrderByMatchRoundStartTime(ServerRequest request) {
    return matchService.findAllByOrderByMatchRoundStartTime()
        .flatMap(matches -> ServerResponse.ok().bodyValue(matches))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  @Override
  public Mono<ServerResponse> findById(ServerRequest request) {
    var id = Long.valueOf(request.pathVariable("id"));
    return matchService.findById(id)
        .flatMap(match -> ServerResponse.ok().bodyValue(match))
        .switchIfEmpty(ServerResponse.notFound().build());
  }
}
