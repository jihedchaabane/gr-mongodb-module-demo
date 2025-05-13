Feature: Product Controller
  As an API user
  I want to manage products via the API
  So that I can maintain the product catalog

  Background:
    Given the product and category collections are empty
    Given a category exists with ID "cat1" and name "Electronics"

  Scenario: Create a new product
    When I send a POST request to "/api/products" with body:
      """
      {
        "id": "prod1",
        "name": "Smartphone",
        "price": 599.99,
        "stockQuantity": 50,
        "category": {
          "categoryId": "cat1",
          "categoryName": "Electronics"
        }
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "id": "prod1",
        "name": "Smartphone",
        "price": 599.99,
        "stockQuantity": 50,
        "category": {
          "categoryId": "cat1",
          "categoryName": "Electronics"
        }
      }
      """

  Scenario: Create a product with negative price
    When I send a POST request to "/api/products" with body:
      """
      {
        "id": "prod1",
        "name": "Smartphone",
        "price": -100,
        "stockQuantity": 50,
        "category": {
          "categoryId": "cat1",
          "categoryName": "Electronics"
        }
      }
      """
    Then the response status code should be 400

  Scenario: Get a product by ID
    Given a product exists with ID "prod1", name "Smartphone", price 599.99, stock 50, and category ID "cat1"
    When I send a GET request to "/api/products/prod1"
    Then the response status code should be 200
    And the response body should contain:
      """
      {
				"id": "prod1",
        "name": "Smartphone",
        "price": 599.99,
				"stockQuantity": 50,
				"category" : {
					"categoryId": "cat1",
					"categoryName": "Electronics"
				}
			}
      """

  Scenario: Get a non-existent product
    When I send a GET request to "/api/products/prod999"
    Then the response status code should be 404

  Scenario: Update a product
    Given a product exists with ID "prod1", name "Smartphone", price 599.99, stock 50, and category ID "cat1"
    When I send a PUT request to "/api/products/prod1" with body:
      """
      {
        "id": "prod1",
        "name": "Smartphone Updated",
        "price": 699.99,
        "stockQuantity": 40,
        "category": {
          "categoryId": "cat1",
          "categoryName": "Electronics"
        }
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
				"id": "prod1",
				"name": "Smartphone Updated",
				"price": 699.99,
				"stockQuantity": 40,
				"category": {
					"categoryId": "cat1",
					"categoryName": "Electronics"
				}
			}
      """

  Scenario: Delete a product
    Given a product exists with ID "prod1", name "Smartphone", price 599.99, stock 50, and category ID "cat1"
    When I send a DELETE request to "/api/products/prod1"
    Then the response status code should be 204

  Scenario: Find products by price range
    Given a product exists with ID "prod1", name "Smartphone", price 599.99, stock 50, and category ID "cat1"
    When I send a GET request to "/api/products/price-range?minPrice=500&maxPrice=1000"
    Then the response status code should be 200
    And the response body should contain an array with 1 element