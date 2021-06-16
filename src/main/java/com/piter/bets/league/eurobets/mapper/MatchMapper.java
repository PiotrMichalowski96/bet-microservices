package com.piter.bets.league.eurobets.mapper;

import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.entity.MatchResult;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MatchMapper {

  @Mapping(target = "homeTeamGoals", source = "matchResult.homeTeamGoals")
  @Mapping(target = "awayTeamGoals", source = "matchResult.awayTeamGoals")
  @Mapping(target = "matchRoundId", source = "matchRound.id")
  MatchDTO toMatchDTO(Match match);

  Match toMatch(MatchDTO matchDTO);

  @Mapping(target = "id", ignore = true)
  MatchResult toMatchResult(MatchDTO matchDTO);

  @AfterMapping
  default void callToMatch(@MappingTarget Match match, MatchDTO matchDTO) {
    MatchResult matchResult = toMatchResult(matchDTO);
    matchResult.setMatch(match);
    match.setMatchResult(matchResult);
  }
}
