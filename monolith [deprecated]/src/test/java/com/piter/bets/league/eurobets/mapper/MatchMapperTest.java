package com.piter.bets.league.eurobets.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.util.TestDataUtils;
import org.junit.jupiter.api.Test;

public class MatchMapperTest {

  private final MatchMapper matchMapper = new MatchMapperImpl();

  @Test
  public void shouldCorrectMapMatchToDTO() {
    //given
    Match match = TestDataUtils.generateMatch(1L);

    //when
    MatchDTO matchDTO = matchMapper.toMatchDTO(match);

    //then
    assertThat(matchDTO.getId()).isEqualTo(match.getId());
    assertThat(matchDTO.getHomeTeam()).isEqualTo(match.getHomeTeam());
    assertThat(matchDTO.getAwayTeam()).isEqualTo(match.getAwayTeam());

    assertThat(matchDTO.getMatchRoundId()).isEqualTo(match.getMatchRound().getId());

    assertThat(matchDTO.getAwayTeamGoals()).isEqualTo(match.getMatchResult().getAwayTeamGoals());
    assertThat(matchDTO.getHomeTeamGoals()).isEqualTo(match.getMatchResult().getHomeTeamGoals());
  }

  @Test
  public void shouldCorrectMapDtoToMatchEntityWithMatchResult() {
    //given
    MatchDTO matchDTO = TestDataUtils.generateMatchDTO(1L);

    //when
    Match match = matchMapper.toMatch(matchDTO);

    //then
    assertThat(match.getId()).isEqualTo(matchDTO.getId());
    assertThat(match.getHomeTeam()).isEqualTo(matchDTO.getHomeTeam());
    assertThat(match.getAwayTeam()).isEqualTo(matchDTO.getAwayTeam());
    assertThat(match.getMatchStartTime()).isEqualTo(matchDTO.getMatchStartTime());

    assertThat(match.getMatchResult()).isNotNull();
    assertThat(match.getMatchResult().getHomeTeamGoals()).isEqualTo(matchDTO.getHomeTeamGoals());
    assertThat(match.getMatchResult().getAwayTeamGoals()).isEqualTo(matchDTO.getAwayTeamGoals());
  }

  @Test
  public void shouldCorrectMapDtoToMatchEntityWithoutMatchResult() {
    //given
    MatchDTO matchDTO = TestDataUtils.generateMatchDTO(1L);
    matchDTO.setAwayTeamGoals(null);
    matchDTO.setHomeTeamGoals(null);

    //when
    Match match = matchMapper.toMatch(matchDTO);

    //then
    assertThat(match.getId()).isEqualTo(matchDTO.getId());
    assertThat(match.getHomeTeam()).isEqualTo(matchDTO.getHomeTeam());
    assertThat(match.getAwayTeam()).isEqualTo(matchDTO.getAwayTeam());
    assertThat(match.getMatchStartTime()).isEqualTo(matchDTO.getMatchStartTime());

    assertThat(match.getMatchResult()).isNull();
  }
}
