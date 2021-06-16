package com.piter.bets.league.eurobets.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "match_round")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchRound {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "round_name")
  private String roundName;

  @Column(name = "start_round_time")
  private LocalDateTime startRoundTime;
}
