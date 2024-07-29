import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return match list ordered by match time"

    request {
        method GET()
        url("/api/v2/matches?order=match-time-desc")
    }

    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body("""
                    [
                      {
                        "id": 1,
                        "home_team": "Argentina",
                        "away_team": "France",
                        "start_time": "2022-12-18 16:00:00",
                        "result": {
                          "home_team_goals": 3,
                          "away_team_goals": 3
                        },
                        "round": {
                          "round_name": "Final",
                          "start_time": "2022-12-18 16:00:00"
                        }
                      },
                      {
                        "id": 2,
                        "home_team": "Argentina",
                        "away_team": "Croatia",
                        "start_time": "2022-12-13 20:00:00",
                        "result": {
                          "home_team_goals": 3,
                          "away_team_goals": 0
                        },
                        "round": {
                          "round_name": "Semi-finals",
                          "start_time": "2022-12-13 20:00:00"
                        }
                      },
                      {
                        "id": 3,
                        "home_team": "Marocco",
                        "away_team": "France",
                        "start_time": "2022-12-14 20:00:00",
                        "result": {
                          "home_team_goals": 2,
                          "away_team_goals": 0
                        },
                        "round": {
                          "round_name": "Semi-finals",
                          "start_time": "2022-12-13 20:00:00"
                        }
                      }
                    ]
                """)
    }
}