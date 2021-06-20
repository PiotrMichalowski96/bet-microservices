package com.piter.bets.league.eurobets.mapper;

import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.entity.Bet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BetMapper {

  @Mapping(target = "matchId", source = "match.id")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "homeTeamResult", source = "betResults.homeTeamResult")
  @Mapping(target = "points", source = "betResults.points")
  BetDTO toBetDTO(Bet bet);

  @Mapping(target = "id", ignore = true)
  Bet toBet(BetDTO betDTO);
}
