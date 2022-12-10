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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Bet_Results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BetResults {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "points")
  private Long points;

  @OneToOne
  @MapsId
  @JoinColumn(name = "bet_id")
  private Bet bet;
}
