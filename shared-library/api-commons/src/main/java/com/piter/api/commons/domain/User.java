package com.piter.api.commons.domain;

import jakarta.validation.constraints.NotBlank;
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
