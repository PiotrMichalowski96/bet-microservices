export interface Match {
  id: number;
  homeTeam: string;
  awayTeam: string;
  startTime: Date;
  result: MatchResult | null;
  round: MatchRound;
}

export class MatchResult {
  homeTeamGoals: number;
  awayTeamGoals: number;

  constructor(homeTeamGoals: number, awayTeamGoals: number) {
    this.homeTeamGoals = homeTeamGoals;
    this.awayTeamGoals = awayTeamGoals;
  }
}

export interface MatchRound {
  roundName: string;
  startTime: Date;
}
