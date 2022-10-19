import { Component, OnInit } from '@angular/core';
import {Match, MatchResult, MatchRound} from "../../model/match";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  notFinishedMatches: Match[] = [];

  constructor() { }

  ngOnInit(): void {
    //TODO: hardcoded matches
    this.notFinishedMatches = [new class implements Match {
      awayTeam: string = 'Arsenal';
      homeTeam: string = 'Leeds United';
      id: number = 1;
      result: MatchResult = new class implements MatchResult {
        awayTeamGoals: number = 1;
        homeTeamGoals: number = 1;
      };
      round: MatchRound = new class implements MatchRound {
        roundName: string = 'Group Stage';
        startTime: Date = new Date();
      }
      startTime: Date = new Date();
    },
      new class implements Match {
        awayTeam: string = 'Manchester United';
        homeTeam: string = 'Liverpool';
        id: number = 2;
        result: MatchResult = new class implements MatchResult {
          awayTeamGoals: number = 1;
          homeTeamGoals: number = 1;
        };
        round: MatchRound = new class implements MatchRound {
          roundName: string = 'Final';
          startTime: Date = new Date();
        }
        startTime: Date = new Date();
      },
      new class implements Match {
        awayTeam: string = 'Manchester United';
        homeTeam: string = 'Liverpool';
        id: number = 2;
        result: MatchResult = new class implements MatchResult {
          awayTeamGoals: number = 1;
          homeTeamGoals: number = 1;
        };
        round: MatchRound = new class implements MatchRound {
          roundName: string = 'Final';
          startTime: Date = new Date();
        }
        startTime: Date = new Date();
      },
      new class implements Match {
        awayTeam: string = 'Manchester United';
        homeTeam: string = 'Liverpool';
        id: number = 2;
        result: MatchResult = new class implements MatchResult {
          awayTeamGoals: number = 1;
          homeTeamGoals: number = 1;
        };
        round: MatchRound = new class implements MatchRound {
          roundName: string = 'Final';
          startTime: Date = new Date();
        }
        startTime: Date = new Date();
      },
      new class implements Match {
        awayTeam: string = 'Manchester United';
        homeTeam: string = 'Liverpool';
        id: number = 2;
        result: MatchResult = new class implements MatchResult {
          awayTeamGoals: number = 1;
          homeTeamGoals: number = 1;
        };
        round: MatchRound = new class implements MatchRound {
          roundName: string = 'Final';
          startTime: Date = new Date();
        }
        startTime: Date = new Date();
      },
      new class implements Match {
        awayTeam: string = 'Manchester United';
        homeTeam: string = 'Liverpool';
        id: number = 2;
        result: MatchResult = new class implements MatchResult {
          awayTeamGoals: number = 1;
          homeTeamGoals: number = 1;
        };
        round: MatchRound = new class implements MatchRound {
          roundName: string = 'Final';
          startTime: Date = new Date();
        }
        startTime: Date = new Date();
      }
      ];
  }

}
