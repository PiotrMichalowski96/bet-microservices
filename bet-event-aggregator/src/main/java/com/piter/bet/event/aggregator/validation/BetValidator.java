package com.piter.bet.event.aggregator.validation;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.BetResult;
import com.piter.bet.event.aggregator.domain.BetResult.Status;
import com.piter.bet.event.aggregator.domain.Match;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BetValidator {

  private static final BetResult UNRESOLVED_BET_RESULTS = BetResult.builder()
      .points(0)
      .status(Status.UNRESOLVED)
      .build();

  private final Validator validator;
  private final List<BetRequestValidator> betValidators;

  public BetValidator(Validator validator) {
    this.validator = validator;
    this.betValidators = List.of(
        this::areRequireFieldsValid,
        this::isMatchNotStarted,
        this::doesNotHaveMatchResult,
        this::doesNotHaveBetResult
    );
  }

  public boolean validate(Bet bet) {
    if (bet == null) {
      return false;
    }
    return betValidators.stream().allMatch(v -> v.isValid(bet));
  }

  private boolean areRequireFieldsValid(Bet bet) {
    Errors errors = new BeanPropertyBindingResult(bet, "bet");
    validator.validate(bet, errors);
    return !errors.hasErrors();
  }

  private boolean isMatchNotStarted(Bet bet) {
    return Optional.ofNullable(bet.match())
        .map(Match::startTime)
        .map(startTime -> LocalDateTime.now().isBefore(startTime))
        .orElse(false);
  }

  private boolean doesNotHaveMatchResult(Bet bet) {
    return Optional.ofNullable(bet.match())
        .map(Match::result)
        .isEmpty();
  }

  private boolean doesNotHaveBetResult(Bet bet) {
    return UNRESOLVED_BET_RESULTS.equals(bet.betResult());
  }

  @FunctionalInterface
  private interface BetRequestValidator {
    boolean isValid(Bet bet);
  }
}
