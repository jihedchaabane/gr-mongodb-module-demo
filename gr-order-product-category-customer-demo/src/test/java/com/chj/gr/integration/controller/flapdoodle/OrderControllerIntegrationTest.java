//package com.chj.gr.integration.controller.flapdoodle;
//
//import com.chj.gr.model.Category;
//import com.chj.gr.model.Customer;
//import com.chj.gr.model.Order;
//import com.chj.gr.model.Product;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.*;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.Arrays;
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Disabled
//public class OrderControllerIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Container
//    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0").withExposedPorts(27017);
//
//    @DynamicPropertySource
//    static void setMongoDbProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl());
//    }
//
//    private String baseUrl;
//    private String customerUrl;
//    private String productUrl;
//    private String categoryUrl;
//
//    @BeforeEach
//    void setUp() {
//        baseUrl = "http://localhost:" + port + "/api/orders";
//        customerUrl = "http://localhost:" + port + "/api/customers";
//        productUrl = "http://localhost:" + port + "/api/products";
//        categoryUrl = "http://localhost:" + port + "/api/categories";
//        restTemplate.delete(baseUrl);
//        restTemplate.delete(customerUrl);
//        restTemplate.delete(productUrl);
//        restTemplate.delete(categoryUrl);
//    }
//
//    @Test
//    void createOrder_ShouldReturnCreatedOrder() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(productUrl, product, Product.class);
//
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(customerUrl, customer, Customer.class);
//
//        Order order = new Order("ord1", new Date(), "En attente", customer, Arrays.asList(product));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(order), headers);
//
//        ResponseEntity<Order> response = restTemplate.postForEntity(baseUrl, request, Order.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("ord1", response.getBody().getOrderId());
//        assertEquals("En attente", response.getBody().getStatus());
//    }
//
//    @Test
//    void createOrder_ShouldReturnBadRequest_WhenCustomerIsMissing() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(productUrl, product, Product.class);
//
//        String invalidJson = "{\"orderId\": \"ord1\", \"orderDate\": \"2025-05-12\", \"status\": \"En attente\", \"products\": [" + objectMapper.writeValueAsString(product) + "]}";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(invalidJson, headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    void getOrderById_ShouldReturnOrder_WhenIdExists() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(productUrl, product, Product.class);
//
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(customerUrl, customer, Customer.class);
//
//        Order order = new Order("ord1", new Date(), "En attente", customer, Arrays.asList(product));
//        restTemplate.postForEntity(baseUrl, order, Order.class);
//
//        ResponseEntity<Order> response = restTemplate.getForEntity(baseUrl + "/ord1", Order.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("ord1", response.getBody().getOrderId());
//    }
//
//    @Test
//    void getOrderById_ShouldReturnNotFound_WhenIdDoesNotExist() {
//        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/ord1", String.class);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void updateOrder_ShouldReturnUpdatedOrder() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(productUrl, product, Product.class);
//
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(customerUrl, customer, Customer.class);
//
//        Order order = new Order("ord1", new Date(), "En attente", customer, Arrays.asList(product));
//        restTemplate.postForEntity(baseUrl, order, Order.class);
//
//        Order updatedOrder = new Order("ord1", new Date(), "Expédiée", customer, Arrays.asList(product));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(updatedOrder), headers);
//
//        ResponseEntity<Order> response = restTemplate.exchange(
//                baseUrl + "/ord1", HttpMethod.PUT, request, Order.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Expédiée", response.getBody().getStatus());
//    }
//
//    @Test
//    void deleteOrder_ShouldReturnNoContent() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(productUrl, product, Product.class);
//
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(customerUrl, customer, Customer.class);
//
//        Order order = new Order("ord1", new Date(), "En attente", customer, Arrays.asList(product));
//        restTemplate.postForEntity(baseUrl, order, Order.class);
//
//        ResponseEntity<Void> response = restTemplate.exchange(
//                baseUrl + "/ord1", HttpMethod.DELETE, null, Void.class);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }
//
//    @Test
//    void findByStatus_ShouldReturnMatchingOrders() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(productUrl, product, Product.class);
//
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(customerUrl, customer, Customer.class);
//
//        Order order = new Order("ord1", new Date(), "En attente", customer, Arrays.asList(product));
//        restTemplate.postForEntity(baseUrl, order, Order.class);
//
//        ResponseEntity<Order[]> response = restTemplate.getForEntity(
//                baseUrl + "/status?status=En attente", Order[].class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().length);
//        assertEquals("ord1", response.getBody()[0].getOrderId());
//    }
//
//    @Test
//    void findByDateRange_ShouldReturnBadRequest_WhenDateFormatIsInvalid() {
//        ResponseEntity<String> response = restTemplate.getForEntity(
//                baseUrl + "/date-range?startDate=invalid-date&endDate=2025-12-31", String.class);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//}