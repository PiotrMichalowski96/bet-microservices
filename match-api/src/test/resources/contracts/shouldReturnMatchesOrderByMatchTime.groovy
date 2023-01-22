import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return match list ordered by match time"

    request {
        method GET()
        url("/matches?order=match-time")
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
                        "homeTeam": "Argentina",
                        "awayTeam": "France",
                        "startTime": "2022-12-18T16:00:00",
                        "result": {
                          "homeTeamGoals": 3,
                          "awayTeamGoals": 3
                        },
                        "round": {
                          "roundName": "Final",
                          "startTime": "2022-12-18T16:00:00"
                        }
                      },
                      {
                        "id": 2,
                        "homeTeam": "Argentina",
                        "awayTeam": "Croatia",
                        "startTime": "2022-12-13T20:00:00",
                        "result": {
                          "homeTeamGoals": 3,
                          "awayTeamGoals": 0
                        },
                        "round": {
                          "roundName": "Semi-finals",
                          "startTime": "2022-12-13T20:00:00"
                        }
                      },
                      {
                        "id": 3,
                        "homeTeam": "Marocco",
                        "awayTeam": "France",
                        "startTime": "2022-12-14T20:00:00",
                        "result": {
                          "homeTeamGoals": 2,
                          "awayTeamGoals": 0
                        },
                        "round": {
                          "roundName": "Semi-finals",
                          "startTime": "2022-12-13T20:00:00"
                        }
                      }
                    ]
                """)
    }
}