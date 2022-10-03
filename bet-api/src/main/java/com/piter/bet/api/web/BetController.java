package com.piter.bet.api.web;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.service.BetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BetController {

  private final BetService betService;
  private final Validator validator;

  @GetMapping("/bets")
  public Flux<Bet> findAll() {
    return betService.findAll();
  }

  @GetMapping("/bets/{id}")
  public Mono<Bet> findById(@PathVariable("id") Long id) {
    return betService.findById(id);
  }

  @PostMapping("/bets")
  public Mono<Bet> save(@RequestBody Bet bet) {
    return Mono.just(bet)
        .doOnNext(this::validate)
        .flatMap(betService::saveBet);
  }

  @DeleteMapping("/bets/{id}")
  public Mono<Void> delete(@PathVariable("id") Long id) {
    return betService.deleteBet(id);
  }

  private void validate(Bet bet) {
    Errors errors = new BeanPropertyBindingResult(bet, "bet");
    validator.validate(bet, errors);

    if(errors.hasErrors()){
      throw new ServerWebInputException(errors.toString());
    }
  }
}
