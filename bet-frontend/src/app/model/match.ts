export interface Match {
  id: number;
  homeTeam: string;
  awayTeam: string;
  startTime: Date;
  result: MatchResult | null;
  round: MatchRound;
}

export interface MatchResult {
  homeTeamGoals: number;
  awayTeamGoals: number;
}

export interface MatchRound {
  roundName: string;
  startTime: Date;
}