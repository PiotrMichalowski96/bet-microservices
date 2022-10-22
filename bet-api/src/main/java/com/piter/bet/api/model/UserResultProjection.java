package com.piter.bet.api.model;

import com.piter.api.commons.domain.User;
import lombok.Value;

@Value
public class UserResultProjection {
  User user;
  Long points;
}
