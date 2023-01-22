import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return match by id"

    request {
        method GET()
        url("/matches/1")
    }

    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body("""
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
                }
                """)
    }
}