package com.piter.match.api.web;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface MatchWebHandler {

  Mono<ServerResponse> findAll(ServerRequest request);

  Mono<ServerResponse> findAllByOrderByMatchStartTime(ServerRequest request);

  Mono<ServerResponse> findAllByOrderByMatchRoundStartTime(ServerRequest request);

  Mono<ServerResponse> findById(ServerRequest request);
}
