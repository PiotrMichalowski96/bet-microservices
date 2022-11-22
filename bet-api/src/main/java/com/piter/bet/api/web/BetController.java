package com.piter.bet.api.web;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.User;
import com.piter.bet.api.service.BetService;
import com.piter.bet.api.util.TokenUtil;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class BetController {

  private static final String ASC_ORDER_MATCH_TIME = "asc";
  private static final String DESC_ORDER_MATCH_TIME = "desc";

  private final BetService betService;
  private final Validator validator;
  private final Map<String, BetFetcher> findAllMapSuppliers;

  public BetController(BetService betService, Validator validator) {
    this.betService = betService;
    this.validator = validator;
    this.findAllMapSuppliers = Map.of(
        ASC_ORDER_MATCH_TIME, betService::findAllByOrderByMatchStartTimeAsc,
        DESC_ORDER_MATCH_TIME, betService::findAllByOrderByMatchStartTimeDesc
    );
  }

  @GetMapping("/bets")
  Flux<Bet> findAll(BearerTokenAuthentication token,
      @RequestParam Optional<Long> matchId,
      @RequestParam Optional<String> userNickname,
      @RequestParam Optional<String> matchOrder) {

    User user = TokenUtil.getUserFrom(token);
    return matchId.map(id -> betService.findAllByMatchId(id, user))
        .or(() -> userNickname.map(nickname -> betService.findAllByUserNickname(nickname, user)))
        .orElse(matchOrder
                .map(findAllMapSuppliers::get)
                .map(betFetcher -> betFetcher.fetch(user))
                .orElse(betService.findAll(user)));
  }

  @GetMapping("/bets/my-own")
  Flux<Bet> findAllByUser(BearerTokenAuthentication token) {
    User user = TokenUtil.getUserFrom(token);
    return betService.findAllByUserNickname(user.getNickname(), user);
  }

  @GetMapping("/bets/{id}")
  Mono<Bet> findById(@PathVariable("id") String id, BearerTokenAuthentication token) {
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
  Mono<Void> delete(@PathVariable("id") String id, BearerTokenAuthentication token) {
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

  @FunctionalInterface
  private interface BetFetcher {
    Flux<Bet> fetch(User user);
  }
}
