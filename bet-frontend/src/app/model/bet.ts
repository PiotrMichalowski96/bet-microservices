import {Match, MatchResult} from "./match";

export class Bet {
  id: string;
  matchPredictedResult: MatchResult;
  match: Match;
  user: User;
  betResult: BetResult;

  constructor(id: string,
              matchPredictedResult: MatchResult,
              match: Match,
              user: User,
              betResult: BetResult) {
    this.id = id;
    this.matchPredictedResult = matchPredictedResult;
    this.match = match;
    this.user = user;
    this.betResult = betResult;
  }
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
