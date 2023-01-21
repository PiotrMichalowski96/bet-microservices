import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return match by id"

    request{
        method GET()
        url("/matches/1")
    }

    response {
        status 200
    }
}