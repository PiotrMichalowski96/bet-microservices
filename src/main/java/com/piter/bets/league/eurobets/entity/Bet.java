package com.piter.bets.league.eurobets.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Bets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "home_team_goal_bet")
  private Integer homeTeamGoalBet;

  @Column(name = "away_team_goal_bet")
  private Integer awayTeamGoalBet;

  @ManyToOne
  @JoinColumn(name = "match_id")
  @JsonBackReference
  private Match match;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  @OneToOne(mappedBy = "bet", cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  private BetResults betResults;
}
