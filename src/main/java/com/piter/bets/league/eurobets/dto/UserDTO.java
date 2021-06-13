package com.piter.bets.league.eurobets.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDTO {
  private Long id;
  private String username;
  private Integer points;
}
