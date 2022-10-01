package com.piter.bet.api.util;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.domain.BetResult;
import com.piter.bet.api.domain.BetResult.Status;
import com.piter.bet.api.domain.Match;
import com.piter.bet.api.domain.MatchResult;
import com.piter.bet.api.domain.MatchRound;
import com.piter.bet.api.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BetTestData {

  public static List<Bet> createBetList() {
    return List.of(
        Bet.builder()
            .id(1L)
            .matchPredictedResult(new MatchResult(1, 1))
            .user(new User("Jon", "Snow", "snowboard"))
            .betResult(new BetResult(Status.UNRESOLVED, 0))
            .match(Match.builder()
                .id(1L)
                .homeTeam("FC Barcelona")
                .awayTeam("Real Madrid")
                .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
                .result(new MatchResult(4, 0))
                .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
                .build())
            .build(),
        Bet.builder()
            .id(2L)
            .matchPredictedResult(new MatchResult(3, 1))
            .user(new User("Tyrion", "Lanister", "bigGuy"))
            .betResult(new BetResult(Status.CORRECT, 3))
            .match(Match.builder()
                .id(1L)
                .homeTeam("FC Barcelona")
                .awayTeam("Real Madrid")
                .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
                .result(new MatchResult(4, 0))
                .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
                .build())
            .build()
    );
  }
}
