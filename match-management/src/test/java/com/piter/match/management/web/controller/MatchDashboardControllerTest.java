package com.piter.match.management.web.controller;

import static com.piter.match.management.util.TestDataUtil.createMatch;
import static com.piter.match.management.util.TestDataUtil.createMatchList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.piter.match.management.domain.Match;
import com.piter.match.management.domain.MatchResult;
import com.piter.match.management.domain.MatchRound;
import com.piter.match.management.web.model.MatchDetails;
import com.piter.match.management.web.service.MatchDetailsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
class MatchDashboardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MatchDetailsService matchDetailsService;

  @Test
  void shouldShowMatches() throws Exception {
    //given
    var expectedViewName = "dashboard";
    List<Match> expectedMatches = createMatchList();
    mockRetrievingMatchList(expectedMatches);

    //when
    ModelAndView mv = mockMvc.perform(get("/dashboard/match").contentType(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    //then
    assertExpectedView(mv, expectedViewName);
    assertExpectedMatches(mv, expectedMatches);
  }

  private void mockRetrievingMatchList(List<Match> matchList) {
    when(matchDetailsService.loadMatches()).thenReturn(matchList);
  }

  private void assertExpectedView(ModelAndView mv, String expectedView) {
    String actualViewName = mv.getViewName();
    assertThat(actualViewName).isEqualTo(expectedView);
  }

  private void assertExpectedMatches(ModelAndView mv, List<Match> expectedMatches) {
    List<Match> actualMatches = (List<Match>) mv.getModel().get("matches");
    assertThat(actualMatches).hasSameSizeAs(expectedMatches);
    assertThat(actualMatches).hasSameElementsAs(expectedMatches);
  }

  @Test
  void shouldShowSingleMatchDetails() throws Exception {
    //given
    var expectedViewName = "match";
    var id = 1L;
    var expectedMatch = createMatch(id);
    mockRetrievingMatch(expectedMatch);

    //when
    ModelAndView mv = mockMvc.perform(get("/dashboard/match/" + id).contentType(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    //then
    assertExpectedView(mv, expectedViewName);
    assertExpectedMatch(mv, expectedMatch);
  }

  private void mockRetrievingMatch(Match match) {
    var id = match.getId();
    var matchDetails = new MatchDetails(match);
    when(matchDetailsService.findMatch(id)).thenReturn(matchDetails);
  }

  private void assertExpectedMatch(ModelAndView mv, Match expectedMatch) {
    var actualMatch = (Match) mv.getModel().get("match");
    assertThat(actualMatch).isEqualTo(expectedMatch);
  }

  @Test
  void shouldShowMatchToCreate() throws Exception {
    //given
    var expectedViewName = "match";
    var emptyMatch = Match.builder()
        .round(new MatchRound())
        .result(new MatchResult())
        .build();

    //when
    ModelAndView mv = mockMvc.perform(get("/dashboard/match/create").contentType(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    //then
    assertExpectedView(mv, expectedViewName);
    assertExpectedMatch(mv, emptyMatch);
  }

  @Test
  void shouldSaveMatch() throws Exception {
    //given
    var expectedViewName = "dashboard";
    var matchToSave = createMatch(1L);
    mockCorrectSaveMatch(matchToSave);
    mockRetrievingMatchList(createMatchList());

    //when
    ModelAndView mv = mockMvc.perform(post("/dashboard/match")
            .flashAttr("match", matchToSave)
            .contentType(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    //then
    verify(matchDetailsService).saveMatch(matchToSave);
    assertExpectedView(mv, expectedViewName);
  }

  private void mockCorrectSaveMatch(Match match) {
    var matchDetails = new MatchDetails(match);
    when(matchDetailsService.saveMatch(match)).thenReturn(matchDetails);
  }

  @Test
  void shouldReturnErrorPageBecauseFailedSaveMatch() throws Exception {
    //given
    var expectedViewName = "error";
    var matchToSave = createMatch(1L);
    mockFailedSaveMatch(matchToSave);

    //when
    ModelAndView mv = mockMvc.perform(post("/dashboard/match")
            .flashAttr("match", matchToSave)
            .contentType(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    //then
    verify(matchDetailsService).saveMatch(matchToSave);
    assertExpectedView(mv, expectedViewName);
  }

  private void mockFailedSaveMatch(Match matchToSave) {
    var errorDetails = new MatchDetails("Can not save match.");
    when(matchDetailsService.saveMatch(matchToSave)).thenReturn(errorDetails);
  }
}