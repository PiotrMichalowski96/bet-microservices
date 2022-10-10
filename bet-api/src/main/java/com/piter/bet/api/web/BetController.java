package com.piter.bet.api.web;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.domain.User;
import com.piter.bet.api.service.BetService;
import com.piter.bet.api.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
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

@RestController
@RequiredArgsConstructor
public class BetController {

  private final BetService betService;
  private final Validator validator;

  @GetMapping("/bets")
  Flux<Bet> findAll(BearerTokenAuthentication token) {
    User user = TokenUtil.getUserFrom(token);
    return betService.findAll(user);
  }

  @GetMapping("/bets/{id}")
  Mono<Bet> findById(@PathVariable("id") Long id, BearerTokenAuthentication token) {
    User user = TokenUtil.getUserFrom(token);
    return betService.findById(id, user);
  }

  @PostMapping("/bets")
  Mono<Bet> save(@RequestBody Bet bet, BearerTokenAuthentication token) {
    User user = TokenUtil.getUserFrom(token);
    return Mono.just(bet)
        .map(b -> mapWithUser(b, user))
        .doOnNext(this::validate)
        .flatMap(betService::saveBet);
  }

  @DeleteMapping("/bets/{id}")
  Mono<Void> delete(@PathVariable("id") Long id, BearerTokenAuthentication token) {
    User user = TokenUtil.getUserFrom(token);
    return betService.deleteBet(id, user);
  }

  private Bet mapWithUser(Bet bet, User user) {
    return Bet.builder()
        .id(bet.getId())
        .matchPredictedResult(bet.getMatchPredictedResult())
        .match(bet.getMatch())
        .user(user)
        .betResult(bet.getBetResult())
        .build();
  }

  private void validate(Bet bet) {
    Errors errors = new BeanPropertyBindingResult(bet, "bet");
    validator.validate(bet, errors);

    if(errors.hasErrors()){
      throw new ServerWebInputException(errors.toString());
    }
  }
}
