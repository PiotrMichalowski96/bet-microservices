package com.piter.bet.api.domain;

import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class User {
  @NotBlank
  String firstName;
  @NotBlank
  String lastName;
  @NotBlank
  String nickname;
}
