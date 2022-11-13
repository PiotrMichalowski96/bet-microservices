import {Injectable} from "@angular/core";
import {HttpBackend, HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AppConfig {

  private readonly envFileFolder: string = '././assets/env/';
  private readonly envFileName: string = 'env.json';
  private readonly configFilePrefix: string = 'env-';
  private readonly jsonFileSuffix: string = '.json';

  private config: Config | null = null;
  private http: HttpClient;

  constructor(private handler: HttpBackend) {
    this.http = new HttpClient(handler);
  }

  public getConfig(): Config | null {
    return this.config;
  }

  public load() {
    return new Promise((resolve, reject) => {
      this.http.get<Environment>(this.envFileFolder + this.envFileName).subscribe((envResponse: Environment) => {

        if (envResponse === null) {
          console.error('Environment file is invalid');
          resolve(true);
          return;
        }

        let configFileName: string = this.createConfigFileName(envResponse.env);

        this.http.get<Config>(configFileName).subscribe((responseData) => {
          resolve(true);
          this.config = responseData;
        });
      });
    });
  }

  private createConfigFileName(env: string): string {
    return this.envFileFolder + this.configFilePrefix + env + this.jsonFileSuffix;
  }
}

export interface Environment {
  env: string;
}

export interface Config {
  frontendBaseUrl: string;
  backendBaseUrl: string;
  clientId: string;
  authorizationUri: string;
  tokenUri: string;
}
