package com.piter.match.service.rest;

import com.piter.match.service.domain.Match;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "match-api", url = "${match-api.url}")
public interface MatchApiClient {

  @GetMapping(value = "/match/{id}")
  Match findMatchById(@PathVariable Long id);

  @GetMapping(value = "/match")
  List<Match> findMatches(@RequestParam String order);
}
