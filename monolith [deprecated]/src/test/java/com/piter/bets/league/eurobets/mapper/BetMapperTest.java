package com.piter.bets.league.eurobets.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.util.TestDataUtils;
import org.junit.jupiter.api.Test;

public class BetMapperTest {

  private final BetMapper betMapper = new BetMapperImpl();

  @Test
  public void shouldMapBetToDTO() {
    //given
    Bet bet = TestDataUtils.generateBet(1L);

    //when
    BetDTO betDTO = betMapper.toBetDTO(bet);

    //then
    assertThat(betDTO.getId()).isEqualTo(bet.getId());
    assertThat(betDTO.getMatchId()).isEqualTo(bet.getMatch().getId());
    assertThat(betDTO.getUserId()).isEqualTo(bet.getUser().getId());
    assertThat(betDTO.getHomeTeamGoalBet()).isEqualTo(bet.getHomeTeamGoalBet().longValue());
    assertThat(betDTO.getAwayTeamGoalBet()).isEqualTo(bet.getAwayTeamGoalBet().longValue());
    assertThat(betDTO.getPoints()).isEqualTo(bet.getBetResults().getPoints());
  }

  @Test
  public void shouldMapDtoToBetEntity() {
    //given
    BetDTO betDTO = TestDataUtils.generateBetDTO(1L);

    //when
    Bet bet = betMapper.toBet(betDTO);

    //then
    assertThat(bet.getHomeTeamGoalBet()).isEqualTo(betDTO.getHomeTeamGoalBet().intValue());
    assertThat(bet.getAwayTeamGoalBet()).isEqualTo(betDTO.getAwayTeamGoalBet().intValue());
  }
}
