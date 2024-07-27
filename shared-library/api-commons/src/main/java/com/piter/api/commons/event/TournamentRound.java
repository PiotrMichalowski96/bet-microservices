package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.piter.api.commons.model.MatchRound;
import java.time.LocalDateTime;

public record TournamentRound(
    @JsonProperty("name")
    String name,
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime
) {

  public MatchRound toMatchRound() {
    return new MatchRound(name, startTime);
  }

  public static TournamentRound of(MatchRound round) {
    if (round == null) {
      return null;
    }
    return new TournamentRound(round.roundName(), round.startTime());
  }
}
