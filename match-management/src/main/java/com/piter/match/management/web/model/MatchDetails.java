package com.piter.match.management.web.model;

import com.piter.match.management.domain.Match;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MatchDetails {
  private final Match match;
  private final ErrorDetails errorDetails;

  public MatchDetails(Match match) {
    this.match = match;
    this.errorDetails = null;
  }

  public MatchDetails(String errorMessage) {
    this.errorDetails = new ErrorDetails(errorMessage);
    this.match = null;
  }
}
