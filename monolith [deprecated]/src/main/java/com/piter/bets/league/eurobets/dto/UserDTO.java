package com.piter.bets.league.eurobets.dto;

import javax.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDTO {

  private Long id;
  @NotBlank
  private String username;
  @NotBlank
  private Integer points;
}
