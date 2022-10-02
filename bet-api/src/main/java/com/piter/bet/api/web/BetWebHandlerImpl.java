package com.piter.bet.api.web;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.exception.BetNotFoundException;
import com.piter.bet.api.service.BetService;
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
public class BetWebHandlerImpl implements BetWebHandler {

  private final BetService betService;
  private final Validator validator;

  @Override
  public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
    return ServerResponse.ok().body(betService.findAll(), Bet.class);
  }

  @Override
  public Mono<ServerResponse> findById(ServerRequest request) {
    var id = Long.valueOf(request.pathVariable("id"));
    return betService.findById(id)
        .flatMap(bet -> ServerResponse.ok().bodyValue(bet))
        .onErrorResume(e -> e instanceof BetNotFoundException, e -> ServerResponse.notFound().build());
  }

  @Override
  public Mono<ServerResponse> saveBet(ServerRequest request) {
    return request.bodyToMono(Bet.class)
        .doOnNext(this::validate)
        .flatMap(betService::saveBet)
        .flatMap(match -> ServerResponse.ok().bodyValue(match));
  }

  private void validate(Bet bet) {
    Errors errors = new BeanPropertyBindingResult(bet, "bet");
    validator.validate(bet, errors);

    if(errors.hasErrors()){
      throw new ServerWebInputException(errors.toString());
    }
  }

  @Override
  public Mono<ServerResponse> deleteBet(ServerRequest request) {
    var id = Long.valueOf(request.pathVariable("id"));
    return betService.deleteBet(id)
        .flatMap(voidMono -> ServerResponse.ok().build())
        .onErrorResume(e -> e instanceof BetNotFoundException, e -> ServerResponse.notFound().build());
  }
}
