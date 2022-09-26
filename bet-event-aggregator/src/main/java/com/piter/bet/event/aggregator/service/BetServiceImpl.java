package com.piter.bet.event.aggregator.service;

import static com.piter.bet.event.aggregator.service.HomeTeamResult.getResult;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.BetResult;
import com.piter.bet.event.aggregator.domain.BetResult.Status;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.prediction.BetPredictionFetcher;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BetServiceImpl implements BetService {

  private final BetPredictionFetcher betPredictionFetcher;

  @Override
  public Bet fetchBetResult(Bet bet) {
    BetResult betResult = isPredictedMatchResultCorrect(bet) ?
        BetResult.builder()
            .points(betPredictionFetcher.fetchPointsForPrediction(bet))
            .status(Status.CORRECT)
            .build() :
        BetResult.builder()
            .points(0)
            .status(Status.INCORRECT)
            .build();

    return mapBet(bet, betResult);
  }

  private boolean isPredictedMatchResultCorrect(Bet bet) {
    HomeTeamResult predictedResult = getResult(bet.getMatchPredictedResult());
    HomeTeamResult actualResult = getActualResult(bet.getMatch());
    return predictedResult.equals(actualResult);
  }

  private HomeTeamResult getActualResult(Match match) {
    return Optional.ofNullable(match.getResult())
        .filter(matchResult -> Objects.nonNull(matchResult.getHomeTeamGoals()))
        .filter(matchResult -> Objects.nonNull(matchResult.getAwayTeamGoals()))
        .map(HomeTeamResult::getResult)
        .orElseThrow(() -> new RuntimeException("This case is not possible")); //TODO: generic
  }

  private Bet mapBet(Bet bet, BetResult betResult) {
    return Bet.builder()
        .id(bet.getId())
        .matchPredictedResult(bet.getMatchPredictedResult())
        .match(bet.getMatch())
        .user(bet.getUser())
        .betResult(betResult)
        .build();
  }
}
