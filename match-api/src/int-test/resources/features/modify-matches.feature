Feature: Testing a Match REST API. Users should be able to create, update and delete matches.

  Scenario Outline: Create new match
    Given match <MatchPath> does not exist in service
    When user sends post request with match <MatchPath>
    Then match service return response with status 200
    And match <MatchPath> is saved and retrieved in service
    Examples:
      | MatchPath                             |
      | "modify_scenario/create_match.json"   |
      | "modify_scenario/create_match_2.json" |

  Scenario Outline: Invalid match to create
    Given match <MatchPath> does not exist in service
    When user sends post request with match <MatchPath>
    Then match service return response with status 400
    And match <MatchPath> is not present in service
    Examples:
      | MatchPath                                     |
      | "modify_scenario/invalid_create_match.json"   |
      | "modify_scenario/invalid_create_match_2.json" |
      | "modify_scenario/invalid_create_match_3.json" |

  Scenario Outline: Update existing match
    Given match <ExistingMatchPath> is saved in database
    And match <ExistingMatchPath> exists in service
    When user sends post request with match <UpdatedMatchPath>
    Then match service return response with status 200
    And match <UpdatedMatchPath> is saved and retrieved in service
    Examples:
      | ExistingMatchPath                     | UpdatedMatchPath                    |
      | "modify_scenario/existing_match.json" | "modify_scenario/update_match.json" |

  Scenario Outline: Invalid match to update
    Given match <ExistingMatchPath> is saved in database
    And match <ExistingMatchPath> exists in service
    When user sends post request with match <UpdatedMatchPath>
    Then match service return response with status 400
    And match <ExistingMatchPath> is saved and retrieved in service
    Examples:
      | ExistingMatchPath                       | UpdatedMatchPath                              |
      | "modify_scenario/existing_match_2.json" | "modify_scenario/invalid_update_match_2.json" |

  #TODO: bug in spring cloud was reported: https://github.com/spring-cloud/spring-cloud-function/issues/1060
  @ignore
  Scenario Outline: Delete existing match
    Given match <MatchToDeletePath> is saved in database
    And match <MatchToDeletePath> exists in service
    When user sends request to delete match with ID <Id>
    Then match service return response with status 200
    And match <MatchToDeletePath> is not present in service
    Examples:
      | MatchToDeletePath                   | Id |
      | "modify_scenario/delete_match.json" | 19 |

  Scenario Outline: Delete non existing match
    When user sends request to delete match with ID <Id>
    Then match service return response with status 404
    Examples:
      | Id  |
      | 123 |