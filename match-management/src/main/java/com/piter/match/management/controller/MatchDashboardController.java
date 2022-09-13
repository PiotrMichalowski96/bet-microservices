package com.piter.match.management.controller;

import com.piter.match.management.service.LoadMatchDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("dashboard")
@RequiredArgsConstructor
public class MatchDashboardController {

  private static final String MATCHES_TEMPLATE = "dashboard";

  private final LoadMatchDetailsService loadMatchDetailsService;

  @GetMapping(value = "/matches")
  public ModelAndView showMatches() {

    ModelAndView mv = new ModelAndView();
    mv.setViewName(MATCHES_TEMPLATE);
    mv.addObject("matches", loadMatchDetailsService.loadMatches());
    return mv;
  }
}