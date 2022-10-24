package com.piter.api.commons.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Document
public class Bet {
  @Id
  @NotNull
  String id;
  @NotNull
  @Valid
  MatchResult matchPredictedResult;
  @NotNull
  @Valid
  Match match;
  @NotNull
  @Valid
  User user;
  @NotNull
  @Valid
  BetResult betResult;
}
