package com.piter.bet.event.aggregator.domain;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class User {
  @NotBlank
  String firstName;
  @NotBlank
  String lastName;
  @NotBlank
  String nickname;
}
