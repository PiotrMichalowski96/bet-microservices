package com.piter.bets.league.eurobets.controller;

import static com.piter.bets.league.eurobets.util.TestConvertionUtils.convertToJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.service.BetService;
import com.piter.bets.league.eurobets.util.TestDataUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BetService betService;

  @WithUserDetails(value = "user")
  @Test
  public void shouldReturnBetWithGivenId() throws Exception {
    //given
    Long betId = 1L;
    BetDTO betDTO = TestDataUtils.generateBetDTO(betId);
    when(betService.findById(eq(betId), anyLong())).thenReturn(betDTO);

    //whenThen
    mockMvc.perform(get("/bets/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(betDTO.getId().intValue())))
        .andExpect(jsonPath("$.matchId", is(betDTO.getMatchId().intValue())))
        .andExpect(jsonPath("$.userId", is(betDTO.getUserId().intValue())))
        .andExpect(jsonPath("$.homeTeamGoalBet", is(betDTO.getHomeTeamGoalBet().intValue())))
        .andExpect(jsonPath("$.awayTeamGoalBet", is(betDTO.getAwayTeamGoalBet().intValue())))
        .andExpect(jsonPath("$.points", is(betDTO.getPoints().intValue())));
  }

  @WithUserDetails(value = "user")
  @Test
  public void shouldReturnBetsWithGivenUserId() throws Exception {
    //given
    Long userId = 1L;
    Integer pageNumber = 0;

    BetDTO firstBet = TestDataUtils.generateBetDTO(1L);
    BetDTO secondBet = TestDataUtils.generateBetDTO(2L);
    List<BetDTO> bets= List.of(firstBet, secondBet);

    when(betService.findAllByUserId(eq(pageNumber), eq(userId), anyLong())).thenReturn(bets);

    //whenThen
    mockMvc.perform(get("/bets/user/1?pageNumber=0")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].id", is(firstBet.getId().intValue())))
        .andExpect(jsonPath("$[0].matchId", is(firstBet.getMatchId().intValue())))
        .andExpect(jsonPath("$[0].userId", is(firstBet.getUserId().intValue())))
        .andExpect(jsonPath("$[0].homeTeamGoalBet", is(firstBet.getHomeTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[0].awayTeamGoalBet", is(firstBet.getAwayTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[0].points", is(firstBet.getPoints().intValue())))

        .andExpect(jsonPath("$[1].id", is(secondBet.getId().intValue())))
        .andExpect(jsonPath("$[1].matchId", is(secondBet.getMatchId().intValue())))
        .andExpect(jsonPath("$[1].userId", is(secondBet.getUserId().intValue())))
        .andExpect(jsonPath("$[1].homeTeamGoalBet", is(secondBet.getHomeTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[1].awayTeamGoalBet", is(secondBet.getAwayTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[1].points", is(secondBet.getPoints().intValue())));
  }

  @WithUserDetails(value = "user")
  @Test
  public void shouldReturnAllVisibleBets() throws Exception {
    //given
    Integer pageNumber = 0;

    BetDTO firstBet = TestDataUtils.generateBetDTO(1L);
    BetDTO secondBet = TestDataUtils.generateBetDTO(2L);
    List<BetDTO> bets= List.of(firstBet, secondBet);

    when(betService.findAllThatShouldBeVisible(eq(pageNumber), anyLong())).thenReturn(bets);

    //whenThen
    mockMvc.perform(get("/bets?pageNumber=0")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].id", is(firstBet.getId().intValue())))
        .andExpect(jsonPath("$[0].matchId", is(firstBet.getMatchId().intValue())))
        .andExpect(jsonPath("$[0].userId", is(firstBet.getUserId().intValue())))
        .andExpect(jsonPath("$[0].homeTeamGoalBet", is(firstBet.getHomeTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[0].awayTeamGoalBet", is(firstBet.getAwayTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[0].points", is(firstBet.getPoints().intValue())))

        .andExpect(jsonPath("$[1].id", is(secondBet.getId().intValue())))
        .andExpect(jsonPath("$[1].matchId", is(secondBet.getMatchId().intValue())))
        .andExpect(jsonPath("$[1].userId", is(secondBet.getUserId().intValue())))
        .andExpect(jsonPath("$[1].homeTeamGoalBet", is(secondBet.getHomeTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[1].awayTeamGoalBet", is(secondBet.getAwayTeamGoalBet().intValue())))
        .andExpect(jsonPath("$[1].points", is(secondBet.getPoints().intValue())));
  }

  @WithUserDetails(value = "user")
  @Test
  public void shouldSaveBet()
      throws Exception {
    //given
    Long matchId = 1L;
    BetDTO betDTO = TestDataUtils.generateBetDTO(1L);
    when(betService.save(anyLong(), eq(matchId), eq(betDTO))).thenReturn(betDTO);

    //whenThen
    mockMvc.perform(post("/bets/match/1")
        .content(convertToJsonString(betDTO))
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8")
        .accept(MediaType.APPLICATION_JSON)
        .with(csrf()))

        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(betDTO.getId().intValue())))
        .andExpect(jsonPath("$.matchId", is(betDTO.getMatchId().intValue())))
        .andExpect(jsonPath("$.userId", is(betDTO.getUserId().intValue())))
        .andExpect(jsonPath("$.homeTeamGoalBet", is(betDTO.getHomeTeamGoalBet().intValue())))
        .andExpect(jsonPath("$.awayTeamGoalBet", is(betDTO.getAwayTeamGoalBet().intValue())))
        .andExpect(jsonPath("$.points", is(betDTO.getPoints().intValue())));
  }

  @WithUserDetails(value = "user")
  @Test
  public void shouldDeleteBet() throws Exception {
    //given default
    //whenThen
    mockMvc.perform(delete("/bets/1")
        .with(csrf()))
        .andExpect(status().isAccepted());
  }
}
