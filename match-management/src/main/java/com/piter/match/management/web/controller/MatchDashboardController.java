package com.piter.match.management.web.controller;

import com.piter.match.management.domain.Match;
import com.piter.match.management.web.model.ErrorDetails;
import com.piter.match.management.web.model.MatchDetails;
import com.piter.match.management.web.service.MatchDetailsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("dashboard")
@RequiredArgsConstructor
public class MatchDashboardController {

  private static final String MATCHES_TEMPLATE = "dashboard";
  private static final String MATCH_TEMPLATE = "match";
  private static final String ERROR_TEMPLATE = "error";

  private final MatchDetailsService matchDetailsService;

  @GetMapping(value = "/match")
  ModelAndView showMatches() {
    List<Match> matches = matchDetailsService.loadMatches();
    if (matches.isEmpty()) {
      String errorMessage = "Match list is empty.";
      return errorModelAndView(errorMessage);
    }
    return matchListModelAndView(matches);
  }

  @GetMapping(value = "/match/{id}")
  ModelAndView showMatch(@PathVariable Long id) {
    MatchDetails matchDetails = matchDetailsService.findMatch(id);
    if (matchDetails.hasErrors()) {
      ErrorDetails errorDetails = matchDetails.getErrorDetails();
      return errorModelAndView(errorDetails);
    }
    Match foundMatch = matchDetails.getMatch();
    return matchModelAndView(foundMatch);
  }

  @GetMapping(value = "/match/create")
  ModelAndView showMatchToCreate() {
    Match emptyMatch = new Match();
    return matchModelAndView(emptyMatch);
  }

  @PostMapping(value = "/match")
  ModelAndView saveMatch(@ModelAttribute("match") Match match) {
    MatchDetails matchDetails = matchDetailsService.saveMatch(match);
    if (matchDetails.hasErrors()) {
      ErrorDetails errorDetails = matchDetails.getErrorDetails();
      return errorModelAndView(errorDetails);
    }
    return showMatches();
  }

  private ModelAndView matchListModelAndView(List<Match> matches) {
    ModelAndView mv = new ModelAndView();
    mv.setViewName(MATCHES_TEMPLATE);
    mv.addObject("matches", matches);
    return mv;
  }

  private ModelAndView matchModelAndView(Match match) {
    ModelAndView mv = new ModelAndView();
    mv.setViewName(MATCH_TEMPLATE);
    mv.addObject("match", match);
    return mv;
  }

  private ModelAndView errorModelAndView(ErrorDetails errorDetails) {
    return errorModelAndView(errorDetails.getMessage());
  }

  private ModelAndView errorModelAndView(String errorMessage) {
    ModelAndView mv = new ModelAndView();
    mv.setViewName(ERROR_TEMPLATE);
    mv.addObject("errorMessage", errorMessage);
    return mv;
  }
}