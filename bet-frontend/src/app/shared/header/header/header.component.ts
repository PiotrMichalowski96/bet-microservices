import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../services/auth.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  isLoggedIn: boolean = false;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.checkCredentials();
    let i = window.location.href.indexOf('code');
    if(!this.isLoggedIn && i != -1) {
      console.log('Code: ' + window.location.href.substring(i + 5))
      this.authService.retrieveToken(window.location.href.substring(i + 5));
    }
  }

  login() {
    window.location.href = this.authService.authorizationUri;
  }

  logout() {
    this.authService.logout();
  }
}
