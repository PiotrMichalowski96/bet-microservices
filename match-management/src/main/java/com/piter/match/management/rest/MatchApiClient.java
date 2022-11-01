package com.piter.match.management.rest;

import com.piter.match.management.security.OAuth2FeignConfig;
import com.piter.match.management.domain.Match;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "match-api",
    url = "${match-api.url}",
    configuration = OAuth2FeignConfig.class)
public interface MatchApiClient {

  @GetMapping(value = "/matches/{id}")
  Match findMatchById(@PathVariable Long id);

  @GetMapping(value = "/matches")
  List<Match> findMatches(@RequestParam String order);

  @PostMapping(value = "/matches")
  Match saveMatch(Match match);

  @DeleteMapping(value = "/matches/{id}")
  void deleteMatch(@PathVariable Long id);
}
