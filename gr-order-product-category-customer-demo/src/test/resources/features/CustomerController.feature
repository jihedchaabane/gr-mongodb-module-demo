Feature: Customer Controller
  As an API user
  I want to manage customers via the API
  So that I can track customer information

  Background:
    Given the customer collection is empty

  Scenario: Create a new customer
    When I send a POST request to "/api/customers" with body:
      """
      {
        "customerId": "cust1",
        "firstName": "Jean",
        "lastName": "Dupont"
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "customerId": "cust1",
        "firstName": "Jean",
        "lastName": "Dupont"
      }
      """

  Scenario: Create a customer with missing firstName
    When I send a POST request to "/api/customers" with body:
      """
      {
        "customerId": "cust1",
        "lastName": "Dupont"
      }
      """
    Then the response status code should be 400

  Scenario: Get a customer by ID
    Given a customer exists with ID "cust1", first name "Jean", and last name "Dupont"
    When I send a GET request to "/api/customers/cust1"
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "customerId": "cust1",
        "firstName": "Jean",
        "lastName": "Dupont"
      }
      """

  Scenario: Get a non-existent customer
    When I send a GET request to "/api/customers/cust999"
    Then the response status code should be 404

  Scenario: Update a customer
    Given a customer exists with ID "cust1", first name "Jean", and last name "Dupont"
    When I send a PUT request to "/api/customers/cust1" with body:
      """
      {
        "customerId": "cust1",
        "firstName": "Jean",
        "lastName": "Dupont Updated"
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "customerId": "cust1",
        "firstName": "Jean",
        "lastName": "Dupont Updated"
      }
      """

  Scenario: Delete a customer
    Given a customer exists with ID "cust1", first name "Jean", and last name "Dupont"
    When I send a DELETE request to "/api/customers/cust1"
    Then the response status code should be 204

  Scenario: Search customers by partial name
    Given a customer exists with ID "cust1", first name "Jean", and last name "Dupont"
    When I send a GET request to "/api/customers/search?name=jean"
    Then the response status code should be 200
    And the response body should contain an array with 1 element