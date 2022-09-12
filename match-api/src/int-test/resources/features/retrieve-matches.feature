Feature: Testing a Match REST API. Users should be able to retrieve matches.

  Scenario Outline: Get all matches from Mongo database
    When user sends request to get all matches
    Then match service return response with status <ResponseStatus>
    And returned matches are equal to expected <ExpectedMatches>
    Examples:
      | ResponseStatus | ExpectedMatches                                  |
      | 200            | "samples/retrieve_scenario/get_all_matches.json" |

  Scenario Outline: Get all matches from Mongo database ordered by start time
    When user sends request to get matches ordered by "start-time"
    Then match service return response with status <ResponseStatus>
    And returned matches are equal to expected <ExpectedMatches>
    Examples:
      | ResponseStatus | ExpectedMatches                                            |
      | 200            | "samples/retrieve_scenario/get_matches_by_start_time.json" |

  Scenario Outline: Get all matches from Mongo database ordered by round time
    When user sends request to get matches ordered by "round-time"
    Then match service return response with status <ResponseStatus>
    And returned matches are equal to expected <ExpectedMatches>
    Examples:
      | ResponseStatus | ExpectedMatches                                            |
      | 200            | "samples/retrieve_scenario/get_matches_by_round_time.json" |

  Scenario Outline: Find match by id from Mongo database
    When user sends request to find match by ID <Id>
    Then match service return response with status <ResponseStatus>
    And returned match is equal to expected <ExpectedMatch>
    Examples:
      | Id | ResponseStatus | ExpectedMatch                                      |
      | 1  | 200            | "samples/retrieve_scenario/get_match_by_id_1.json" |
      | 2  | 200            | "samples/retrieve_scenario/get_match_by_id_2.json" |
      | 6  | 200            | "samples/retrieve_scenario/get_match_by_id_6.json" |

  Scenario Outline: Try to find match with non-existing ID
    When user sends request to find match by ID <Id>
    Then match service return response with status <ResponseStatus>
    Examples:
      | Id | ResponseStatus |
      | 99 | 404            |