package com.piter.bets.league.eurobets.controller;

import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.exception.BetNotFoundException;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.exception.BettingRulesException;
import com.piter.bets.league.eurobets.exception.UnauthorizedUserException;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.security.UserPrincipal;
import com.piter.bets.league.eurobets.service.BetService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/bets")
@RequiredArgsConstructor
public class BetController {

  private final BetService betService;

  @GetMapping("/{betId}")
  public BetDTO findById(@PathVariable Long betId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) throws BetNotFoundException {

    Long currentUserId = userPrincipal.getUser().getId();
    return betService.findById(betId, currentUserId);
  }

  @GetMapping("/user/{userId}")
  public List<BetDTO> findAllByUserId(@PathVariable Long userId, @RequestParam Integer pageNumber,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {

    Long currentUserId = userPrincipal.getUser().getId();
    return betService.findAllByUserId(pageNumber, userId, currentUserId);
  }

  @GetMapping
  public List<BetDTO> findAllVisible(@RequestParam Integer pageNumber,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {

    Long currentUserId = userPrincipal.getUser().getId();
    return betService.findAllThatShouldBeVisible(pageNumber, currentUserId);
  }

  @PostMapping("/match/{matchId}")
  @ResponseStatus(HttpStatus.CREATED)
  public BetDTO saveBet(@PathVariable Long matchId, @Valid @RequestBody BetDTO betDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws MatchNotFoundException, BettingRulesException, UserNotFoundException {

    Long currentUserId = userPrincipal.getUser().getId();
    return betService.save(currentUserId, matchId, betDTO);
  }

  @DeleteMapping("/{betId}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteBet(@PathVariable Long betId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) throws UnauthorizedUserException {

    Long currentUserId = userPrincipal.getUser().getId();
    betService.delete(betId, currentUserId);
  }
}
