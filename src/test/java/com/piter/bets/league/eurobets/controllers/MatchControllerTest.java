package com.piter.bets.league.eurobets.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.service.MatchService;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class MatchControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MatchService matchService;

  @Test
  public void shouldReturnMatchWithGivenId() throws Exception {
    //given
    MatchDTO match = generateMatch(1L);

    when(matchService.findById(1L)).thenReturn(match);

    //whenThen
    mockMvc.perform(get("/matches/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(match.getId().intValue())))
        .andExpect(jsonPath("$.matchRoundId", is(match.getMatchRoundId().intValue())))
        .andExpect(jsonPath("$.awayTeam", is(match.getAwayTeam())))
        .andExpect(jsonPath("$.homeTeam", is(match.getHomeTeam())))
        .andExpect(jsonPath("$.matchStartTime", is(match.getMatchStartTime().toString())));
  }

  @Test
  public void shouldReturnBadRequestForNotExistingId() throws Exception {
    //given
    MatchDTO match = generateMatch(1L);
    int notExistingId = 2;

    String message = String.format("Cannot find match with id: %d", notExistingId);
    when(matchService.findById(2L)).thenThrow(new MatchNotFoundException(message));

    String url = String.format("/matches/%d", notExistingId);

    //whenThen
    mockMvc.perform(get(url)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(message)));
  }

  @Test
  public void shouldReturnMatchListOrderedByStartTime() throws Exception {
    //given
    MatchDTO firstMatch = generateMatch(1L);
    MatchDTO secondMatch = generateMatch(2L);

    List<MatchDTO> matches = List.of(firstMatch, secondMatch);

    when(matchService.findAllByMatchStartTime(0)).thenReturn(matches);

    //whenThen
    mockMvc.perform(get("/matches?orderBy=startTime&pageNumber=0")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].id", is(firstMatch.getId().intValue())))
        .andExpect(jsonPath("$[0].matchRoundId", is(firstMatch.getMatchRoundId().intValue())))
        .andExpect(jsonPath("$[0].awayTeam", is(firstMatch.getAwayTeam())))
        .andExpect(jsonPath("$[0].homeTeam", is(firstMatch.getHomeTeam())))
        .andExpect(jsonPath("$[0].matchStartTime", is(firstMatch.getMatchStartTime().toString())))

        .andExpect(jsonPath("$[1].id", is(secondMatch.getId().intValue())))
        .andExpect(jsonPath("$[1].matchRoundId", is(secondMatch.getMatchRoundId().intValue())))
        .andExpect(jsonPath("$[1].awayTeam", is(secondMatch.getAwayTeam())))
        .andExpect(jsonPath("$[1].homeTeam", is(secondMatch.getHomeTeam())))
        .andExpect(jsonPath("$[1].matchStartTime", is(secondMatch.getMatchStartTime().toString())));
  }

  @Test
  public void shouldReturnMatchListOrderedByRound() throws Exception {
    //given
    MatchDTO firstMatch = generateMatch(1L);
    MatchDTO secondMatch = generateMatch(2L);

    List<MatchDTO> matches = List.of(firstMatch, secondMatch);

    when(matchService.findAllByMatchRound(0)).thenReturn(matches);

    //whenThen
    mockMvc.perform(get("/matches?orderBy=round&pageNumber=0")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].id", is(firstMatch.getId().intValue())))
        .andExpect(jsonPath("$[0].matchRoundId", is(firstMatch.getMatchRoundId().intValue())))
        .andExpect(jsonPath("$[0].awayTeam", is(firstMatch.getAwayTeam())))
        .andExpect(jsonPath("$[0].homeTeam", is(firstMatch.getHomeTeam())))
        .andExpect(jsonPath("$[0].matchStartTime", is(firstMatch.getMatchStartTime().toString())))

        .andExpect(jsonPath("$[1].id", is(secondMatch.getId().intValue())))
        .andExpect(jsonPath("$[1].matchRoundId", is(secondMatch.getMatchRoundId().intValue())))
        .andExpect(jsonPath("$[1].awayTeam", is(secondMatch.getAwayTeam())))
        .andExpect(jsonPath("$[1].homeTeam", is(secondMatch.getHomeTeam())))
        .andExpect(jsonPath("$[1].matchStartTime", is(secondMatch.getMatchStartTime().toString())));
  }

  @Test
  public void shouldSaveAnswer() throws Exception {
    //given
    MatchDTO match = generateMatch(1L);

    //whenThen
    mockMvc.perform(post("/matches")
        .content(convertToJsonString(match))
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8")
        .accept(MediaType.APPLICATION_JSON)
        .with(csrf()))
        .andExpect(status().isCreated());
  }

  @Test
  public void shouldDeleteMatch() throws Exception {
    mockMvc.perform(delete("/matches/1")
        .with(csrf()))
        .andExpect(status().isAccepted());
  }

  private String convertToJsonString(Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      return StringUtils.EMPTY;
    }
  }

  private MatchDTO generateMatch(Long matchId) {
    return MatchDTO.builder()
        .id(matchId)
        .matchRoundId(RandomUtils.nextLong(1, 11))
        .awayTeam(RandomStringUtils.randomAlphabetic(10))
        .homeTeam(RandomStringUtils.randomAlphabetic(10))
        .matchStartTime(LocalDateTime.now())
        .build();
  }
}
