import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return match list"

    request {
        method GET()
        url("/api/v2/matches?page-1,size=2")
    }

    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body("""
                    [
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