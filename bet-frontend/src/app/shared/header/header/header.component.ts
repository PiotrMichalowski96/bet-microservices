import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {AppConfig} from "../../../config/app.config";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  isLoggedIn: boolean = false;
  private readonly loginUri: string = '';

  constructor(private appConfig: AppConfig, private authService: AuthService) {
    let config = this.appConfig.getConfig();
    if (config !== null) {
      this.loginUri = config.authorizationUri;
    }
  }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.hasCredentials();
    let i = window.location.href.indexOf('code');
    if (!this.isLoggedIn && i != -1) {
      console.log('Code: ' + window.location.href.substring(i + 5))
      this.authService.retrieveToken(window.location.href.substring(i + 5));
    }
  }

  login(): void {
    window.location.href = this.loginUri;
  }

  logout(): void {
    this.authService.logout();
  }
}
