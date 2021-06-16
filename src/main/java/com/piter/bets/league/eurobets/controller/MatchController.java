package com.piter.bets.league.eurobets.controller;

import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.service.MatchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

  private final MatchService matchService;

  @GetMapping("/{id}")
  public MatchDTO findById(@PathVariable Long id) throws MatchNotFoundException {
    MatchDTO match = matchService.findById(id);
    logger.info("Find by id " + match.toString());
    return match;
  }

  @GetMapping
  public List<MatchDTO> findAll(@RequestParam String orderBy, @RequestParam Integer pageNumber) {
    if(orderBy.equals("startTime")) {
      return matchService.findAllByMatchStartTime(pageNumber);
    } else if (orderBy.equals("round")) {
      //TODO: add find with sort by round
    }
    return matchService.findAll(pageNumber);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MatchDTO saveMatch(@RequestBody MatchDTO matchDTO) {
    return matchService.save(matchDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteMatch(@PathVariable Long id) throws MatchNotFoundException {
    matchService.delete(id);
  }
}
