package com.piter.match.api.web;

import com.piter.match.api.domain.Match;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MatchWebHandlerImpl implements MatchWebHandler {

  private final MatchService matchService;
  private final Validator validator;

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
  public Mono<ServerResponse> saveMatch(ServerRequest request) {
    return request.bodyToMono(Match.class)
        .doOnNext(this::validate)
        .flatMap(matchService::saveMatch)
        .flatMap(match -> ServerResponse.ok().bodyValue(match));
  }

  private void validate(Match match) {
    Errors errors = new BeanPropertyBindingResult(match, "match");
    validator.validate(match, errors);

    if(errors.hasErrors()){
      throw new ServerWebInputException(errors.toString());
    }
  }

  @Override
  public Mono<ServerResponse> deleteMatch(ServerRequest request) {
    var id = Long.valueOf(request.pathVariable("id"));
    return matchService.deleteMatch(id)
        .flatMap(voidMono -> ServerResponse.ok().build())
        .onErrorResume(e -> e instanceof MatchNotFoundException, e -> ServerResponse.notFound().build());
  }
}
