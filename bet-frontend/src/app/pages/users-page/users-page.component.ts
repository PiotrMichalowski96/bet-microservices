import {Component, OnInit} from '@angular/core';
import {UsersService} from "../../services/users.service";
import {UserResult} from "../../model/user";

@Component({
  selector: 'app-users-page',
  templateUrl: './users-page.component.html',
  styleUrls: ['./users-page.component.css']
})
export class UsersPageComponent implements OnInit {

  usersResults: UserResult[] = [];

  constructor(private usersService: UsersService) {
  }

  ngOnInit(): void {
    this.getUsersRanking();
  }

  private getUsersRanking(): void {
    this.usersService.getUsersResults().subscribe(usersResults => {
      this.usersResults = usersResults;
    });
  }
}
