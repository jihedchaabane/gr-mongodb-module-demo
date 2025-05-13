Feature: Order Controller
  As an API user
  I want to manage orders via the API
  So that I can track customer purchases

  Background:
    Given the order, customer, product, and category collections are empty
    Given a category exists with ID "cat1" and name "Electronics"
    Given a product exists with ID "prod1", name "Smartphone", price 599.99, stock 50, and category ID "cat1"
    Given a customer exists with ID "cust1", first name "Jean", and last name "Dupont"

  Scenario: Create a new order
    When I send a POST request to "/api/orders" with body:
      """
      {
        "orderId": "ord1",
        "orderDate": "2025-05-13 16:34:08",
        "status": "En attente",
        "customer": {
          "customerId": "cust1",
          "firstName": "Jean",
          "lastName": "Dupont"
        },
        "products": [
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
        ]
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "orderId": "ord1",
        "orderDate": "2025-05-13 16:34:08",
        "status": "En attente",
        "customer": {
          "customerId": "cust1",
          "firstName": "Jean",
          "lastName": "Dupont"
        },
        "products": [
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
        ]
      }
      """

  Scenario: Create an order with missing customer
    When I send a POST request to "/api/orders" with body:
      """
      {
        "orderId": "ord1",
        "orderDate": "2025-05-13 16:34:08",
        "status": "En attente",
        "products": [
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
        ]
      }
      """
    Then the response status code should be 400

  Scenario: Get an order by ID
    Given an order exists with ID "ord1", status "En attente", customer ID "cust1", and product ID "prod1"
    When I send a GET request to "/api/orders/ord1"
    Then the response status code should be 200
    And the response body should contain:
      """
      {
				"orderId": "ord1",
				"orderDate": "2025-05-13 16:34:08",
				"status": "En attente",
				"customer": {
					"customerId": "cust1",
					"firstName": "Jean",
					"lastName": "Dupont"
				},
				"products": [
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
				]
			}
      """

  Scenario: Get a non-existent order
    When I send a GET request to "/api/orders/ord999"
    Then the response status code should be 404

  Scenario: Update an order
    Given an order exists with ID "ord1", status "En attente", customer ID "cust1", and product ID "prod1"
    When I send a PUT request to "/api/orders/ord1" with body:
      """
      {
        "orderId": "ord1",
        "orderDate": "2025-05-13 16:34:08",
        "status": "Expédiée",
        "customer": {
          "customerId": "cust1",
          "firstName": "Jean",
          "lastName": "Dupont"
        },
        "products": [
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
        ]
      }
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      {
        "orderId": "ord1",
        "orderDate": "2025-05-13 16:34:08",
        "status": "Expédiée",
        "customer": {
          "customerId": "cust1",
          "firstName": "Jean",
          "lastName": "Dupont"
        },
        "products": [
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
        ]
      }
      """

  Scenario: Delete an order
    Given an order exists with ID "ord1", status "En attente", customer ID "cust1", and product ID "prod1"
    When I send a DELETE request to "/api/orders/ord1"
    Then the response status code should be 204

  Scenario: Find orders by status
    Given an order exists with ID "ord1", status "En attente", customer ID "cust1", and product ID "prod1"
    When I send a GET request to "/api/orders/status?status=En attente"
    Then the response status code should be 200
    And the response body should contain an array with 1 element