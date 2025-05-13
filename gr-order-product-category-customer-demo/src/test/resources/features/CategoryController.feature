Feature: Category Controller
  As an API user
  I want to manage categories via the API
  So that I can organize products effectively

  Background:
    Given the category collection is empty

  Scenario: Create a new category
    When I send a POST request to "/api/categories" with body:
      """
      {
        "categoryId": "cat1",
        "categoryName": "Electronics"
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "categoryId": "cat1",
        "categoryName": "Electronics"
      }
      """

  Scenario: Create a category with missing categoryId
    When I send a POST request to "/api/categories" with body:
      """
      {
        "categoryName": "Electronics"
      }
      """
    Then the response status code should be 400

  Scenario: Get a category by ID
    Given a category exists with ID "cat1" and name "Electronics"
    When I send a GET request to "/api/categories/cat1"
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "categoryId": "cat1",
        "categoryName": "Electronics"
      }
      """

  Scenario: Get a non-existent category
    When I send a GET request to "/api/categories/cat999"
    Then the response status code should be 404

  Scenario: Update a category
    Given a category exists with ID "cat1" and name "Electronics"
    When I send a PUT request to "/api/categories/cat1" with body:
      """
      {
        "categoryId": "cat1",
        "categoryName": "Electronics Updated"
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "categoryId": "cat1",
        "categoryName": "Electronics Updated"
      }
      """

  Scenario: Delete a category
    Given a category exists with ID "cat1" and name "Electronics"
    When I send a DELETE request to "/api/categories/cat1"
    Then the response status code should be 204

  Scenario: Search categories by partial name
    Given a category exists with ID "cat1" and name "Electronics"
    When I send a GET request to "/api/categories/search?name=electro"
    Then the response status code should be 200
    And the response body should contain an array with 1 element