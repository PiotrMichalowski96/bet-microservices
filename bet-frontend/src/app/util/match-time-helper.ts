import {Match} from "../model/match";

export class MatchTimeHelper {

  public static isMatchStarted(match: Match | null): boolean {
    if (match == null || match.startTime == null) {
      return true;
    }
    let matchTime: number = new Date(match.startTime).getTime();
    let currentTime: number = new Date().getTime();
    return matchTime < currentTime;
  }
}
