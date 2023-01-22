import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should not found match with id 99"

    request {
        method GET()
        url("/matches/99")
    }

    response {
        status 404
    }
}