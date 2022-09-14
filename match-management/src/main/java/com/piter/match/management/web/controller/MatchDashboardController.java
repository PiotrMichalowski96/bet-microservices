package com.piter.match.management.web.controller;

import com.piter.match.management.domain.Match;
import com.piter.match.management.web.model.MatchDetails;
import com.piter.match.management.web.service.MatchDetailsService;
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

  private final MatchDetailsService matchDetailsService;

  @GetMapping(value = "/match")
  public ModelAndView showMatches() {
    ModelAndView mv = new ModelAndView();
    mv.setViewName(MATCHES_TEMPLATE);
    mv.addObject("matches", matchDetailsService.loadMatches());
    return mv;
  }

  @GetMapping(value = "/match/{id}")
  public ModelAndView showMatch(@PathVariable Long id) {
    MatchDetails matchDetails = matchDetailsService.findMatch(id);
    if (matchDetails.getMatch() == null) {
      throw new RuntimeException("Match is not saved");
    }
    ModelAndView mv = new ModelAndView();
    mv.setViewName(MATCH_TEMPLATE);
    mv.addObject("match", matchDetails.getMatch());
    return mv;
  }

  @PostMapping(value = "/match")
  public ModelAndView saveMatch(@ModelAttribute("match") Match match) {
    MatchDetails matchDetails = matchDetailsService.saveMatch(match);
    if (matchDetails.getMatch() == null) {
      throw new RuntimeException("Match is not saved");
    }
    return showMatches();
  }
}