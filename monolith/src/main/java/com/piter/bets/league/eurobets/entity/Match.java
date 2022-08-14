package com.piter.bets.league.eurobets.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Matches")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "home_team")
    private String homeTeam;

    @Column(name = "away_team")
    private String awayTeam;

    @Column(name = "match_start_time")
    private LocalDateTime matchStartTime;

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private MatchResult matchResult;

    @ManyToOne
    @JoinColumn(name = "match_round_id")
    @JsonBackReference
    private MatchRound matchRound;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<Bet> bets;
}
