import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return match by id"

    request {
        method GET()
        url("/api/v2/matches/1")
    }

    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body("""
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
                }
                """)
    }
}