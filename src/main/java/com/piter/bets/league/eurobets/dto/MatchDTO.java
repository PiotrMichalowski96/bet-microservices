package com.piter.bets.league.eurobets.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MatchDTO {

  private Long id;
  @NotBlank
  private String homeTeam;
  @NotBlank
  private String awayTeam;
  @JsonSerialize(using = ToStringSerializer.class)
  private LocalDateTime matchStartTime;
  private Integer homeTeamGoals;
  private Integer awayTeamGoals;
//  private String roundName;
//  @JsonSerialize(using = ToStringSerializer.class)
//  private LocalDateTime roundStartTime;
}