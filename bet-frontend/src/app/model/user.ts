export interface User {
  firstName: string;
  lastName: string;
  nickname: string;
}

export interface UserResult {
  user: User;
  points: number;
}
