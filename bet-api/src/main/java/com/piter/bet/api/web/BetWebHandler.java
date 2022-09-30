package com.piter.bet.api.web;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface BetWebHandler {

  Mono<ServerResponse> findAll(ServerRequest serverRequest);

  Mono<ServerResponse> findById(ServerRequest request);

  Mono<ServerResponse> saveBet(ServerRequest request);

  Mono<ServerResponse> deleteBet(ServerRequest request);
}
