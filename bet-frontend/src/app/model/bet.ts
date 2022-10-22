import {Match, MatchResult} from "./match";

export interface Bet {
  id: number;
  matchPredictedResult: MatchResult;
  match: Match;
  user: User;
  betResult: BetResult;
}

export interface User {
  firstName: string;
  lastName: string;
  nickname: string;
}

export interface BetResult {
  status: string;
  points: number;
}
