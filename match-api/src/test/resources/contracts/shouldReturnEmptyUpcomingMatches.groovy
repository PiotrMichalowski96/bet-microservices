import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return empty upcoming match list"

    request {
        method GET()
        url("/api/v2/matches/upcoming")
    }

    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body("[]")
    }
}