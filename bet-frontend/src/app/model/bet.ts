import {Match, MatchResult} from "./match";
import {User} from "./user";

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

export class BetResult {
  status: string;
  points: number;

  constructor(status: string, points: number) {
    this.status = status;
    this.points = points;
  }
}
