package com.piter.bets.league.eurobets.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.service.BetService;
import com.piter.bets.league.eurobets.util.TestDataUtils;
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

  private static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @WithUserDetails(value = "Piter") //TODO: change username
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

}
