@InitDb
Feature: Testing a Match REST API. Users should be able to retrieve matches from Mongo database.

  Scenario Outline: Get all matches
    When user sends request to get all matches
    Then match service return response with status 200
    And returned matches are equal to expected <ExpectedMatches>
    Examples:
      | ExpectedMatches                          |
      | "retrieve_scenario/get_all_matches.json" |

  Scenario Outline: Get all matches ordered by start time
    When user sends request to get matches ordered by "start-time"
    Then match service return response with status 200
    And returned matches are equal to expected <ExpectedMatches>
    Examples:
      | ExpectedMatches                                    |
      | "retrieve_scenario/get_matches_by_start_time.json" |

  Scenario Outline: Get all matches ordered by round time
    When user sends request to get matches ordered by "round-time"
    Then match service return response with status 200
    And returned matches are equal to expected <ExpectedMatches>
    Examples:
      | ExpectedMatches                                    |
      | "retrieve_scenario/get_matches_by_round_time.json" |

  Scenario Outline: Find match by id
    When user sends request to find match by ID <Id>
    Then match service return response with status 200
    And returned match is equal to expected <ExpectedMatch>
    Examples:
      | Id | ExpectedMatch                              |
      | 1  | "retrieve_scenario/get_match_by_id_1.json" |
      | 2  | "retrieve_scenario/get_match_by_id_2.json" |
      | 6  | "retrieve_scenario/get_match_by_id_6.json" |

  Scenario Outline: Try to find match with non-existing ID
    When user sends request to find match by ID <Id>
    Then match service return response with status 404
    Examples:
      | Id |
      | 99 |