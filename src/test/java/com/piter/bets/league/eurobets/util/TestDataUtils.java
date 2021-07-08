package com.piter.bets.league.eurobets.util;

import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.entity.BetResults;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.entity.MatchResult;
import com.piter.bets.league.eurobets.entity.MatchRound;
import com.piter.bets.league.eurobets.entity.User;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

@UtilityClass
public class TestDataUtils {

  public static User generateUser(Long userId) {
    return User.builder()
        .id(userId)
        .username(RandomStringUtils.randomAlphabetic(10))
        .passwordHash(RandomStringUtils.randomAlphanumeric(20))
        .points(10L)
        .build();
  }

  public static Match generateMatch(Long matchId) {
    return Match.builder()
        .id(matchId)
        .awayTeam(RandomStringUtils.randomAlphabetic(10))
        .homeTeam(RandomStringUtils.randomAlphabetic(10))
        .matchStartTime(LocalDateTime.now())
        .matchRound(generateMatchRound(RandomUtils.nextLong(0, 10)))
        .matchResult(generateMatchResult(RandomUtils.nextLong(0, 10)))
        .build();
  }

  public static MatchRound generateMatchRound(Long matchRoundId) {
    String roundName = RandomStringUtils.randomAlphabetic(10);
    LocalDateTime startRoundTime = LocalDateTime.now();
    return new MatchRound(matchRoundId, roundName, startRoundTime, null);
  }

  public static MatchResult generateMatchResult(Long matchResultId) {
    return MatchResult.builder()
        .id(matchResultId)
        .homeTeamGoals(RandomUtils.nextInt(0, 10))
        .awayTeamGoals(RandomUtils.nextInt(0, 10))
        .build();
  }

  public static MatchDTO generateMatchDTO(Long matchDtoId) {
    return MatchDTO.builder()
        .id(matchDtoId)
        .awayTeam(RandomStringUtils.randomAlphabetic(10))
        .homeTeam(RandomStringUtils.randomAlphabetic(10))
        .matchStartTime(LocalDateTime.now())
        .homeTeamGoals(RandomUtils.nextInt(0, 10))
        .awayTeamGoals(RandomUtils.nextInt(0, 10))
        .matchRoundId(RandomUtils.nextLong(0, 10))
        .build();
  }

  public static Bet generateBet(Long betId) {
    return Bet.builder()
        .id(betId)
        .homeTeamGoalBet(RandomUtils.nextInt(0, 10))
        .awayTeamGoalBet(RandomUtils.nextInt(0, 10))
        .match(generateMatch(RandomUtils.nextLong(0, 10)))
        .user(generateUser(RandomUtils.nextLong(0, 10)))
        .betResults(generateBetResults(RandomUtils.nextLong(0, 10)))
        .build();
  }

  public static BetResults generateBetResults(Long betResultId) {
    long points = RandomUtils.nextLong(0, 100);
    return new BetResults(betResultId, points, null);
  }

  public static BetDTO generateBetDTO(Long betDtoId) {
    return BetDTO.builder()
        .id(betDtoId)
        .matchId(RandomUtils.nextLong(0, 10))
        .userId(RandomUtils.nextLong(0, 10))
        .homeTeamGoalBet(RandomUtils.nextLong(0, 10))
        .awayTeamGoalBet(RandomUtils.nextLong(0, 10))
        .points(RandomUtils.nextLong(0, 10))
        .build();
  }
}
