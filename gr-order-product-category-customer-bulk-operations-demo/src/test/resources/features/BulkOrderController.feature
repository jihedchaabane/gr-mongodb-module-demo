Feature: Bulk Order Creation
  As an API user
  I want to create multiple orders in bulk
  So that I can efficiently populate the order database

  Background:
    Given the order collection is empty

  Scenario: Create multiple orders in bulk successfully
    When I send a POST request to "/api/bulk-orders" with body:
      """
      [
        {
          "orderId": "ord1",
          "orderDate": "2025-05-13 18:43:00",
          "status": "En attente",
          "customer": {
            "customerId": "cust1",
            "firstName": "Jean",
            "lastName": "Dupont"
          },
          "products": [
            {
              "id": "prod1",
              "name": "Laptop",
              "price": 999.99,
              "stockQuantity": 50,
              "category": {
                "categoryId": "cat1",
                "categoryName": "Electronics"
              }
            }
          ]
        },
        {
          "orderId": "ord2",
          "orderDate": "2025-05-13 18:44:00",
          "status": "En attente",
          "customer": {
            "customerId": "cust2",
            "firstName": "Marie",
            "lastName": "Curie"
          },
          "products": [
            {
              "id": "prod2",
              "name": "Phone",
              "price": 499.99,
              "stockQuantity": 30,
              "category": {
                "categoryId": "cat2",
                "categoryName": "Mobile"
              }
            }
          ]
        }
      ]
      """
    Then the response status code should be 200
    And the response body should contain:
      """
      Successfully saved 2 orders
      """

  Scenario: Fail to create bulk orders with invalid order date
    When I send a POST request to "/api/bulk-orders" with body:
      """
      [
        {
          "orderId": "ord1",
          "orderDate": "2025-05-13", // Invalid Date Format : should be yyyy-MM-dd HH:mm:ss
          "status": "En attente",
          "customer": {
            "customerId": "cust1",
            "firstName": "Jean",
            "lastName": "Dupont"
          },
          "products": [
            {
              "id": "prod1",
              "name": "Laptop",
              "price": 999.99,
              "stockQuantity": 50,
              "category": {
                "categoryId": "cat1",
                "categoryName": "Electronics"
              }
            }
          ]
        }
      ]
      """
    Then the response status code should be 500