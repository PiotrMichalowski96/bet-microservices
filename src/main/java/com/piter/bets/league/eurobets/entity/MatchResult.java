package com.piter.bets.league.eurobets.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "match_results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "home_team_goals")
  private int homeTeamGoals;

  @Column(name = "away_team_goals")
  private int awayTeamGoals;

  @OneToOne
  @MapsId
  @JoinColumn(name = "match_id")
  private Match match;
}
