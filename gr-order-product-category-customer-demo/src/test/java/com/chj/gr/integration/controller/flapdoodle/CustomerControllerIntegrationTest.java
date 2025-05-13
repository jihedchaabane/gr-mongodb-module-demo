//package com.chj.gr.integration.controller.flapdoodle;
//
//import com.chj.gr.model.Customer;
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
//import static org.junit.jupiter.api.Assertions.*;
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Disabled
//public class CustomerControllerIntegrationTest {
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
//
//    @BeforeEach
//    void setUp() {
//        baseUrl = "http://localhost:" + port + "/api/customers";
//        restTemplate.delete(baseUrl);
//    }
//
//    @Test
//    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(customer), headers);
//
//        ResponseEntity<Customer> response = restTemplate.postForEntity(baseUrl, request, Customer.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("cust1", response.getBody().getCustomerId());
//        assertEquals("Jean", response.getBody().getFirstName());
//    }
//
//    @Test
//    void createCustomer_ShouldReturnBadRequest_WhenFirstNameIsMissing() throws Exception {
//        String invalidJson = "{\"customerId\": \"cust1\", \"lastName\": \"Dupont\"}";
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
//    void getCustomerById_ShouldReturnCustomer_WhenIdExists() {
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(baseUrl, customer, Customer.class);
//
//        ResponseEntity<Customer> response = restTemplate.getForEntity(baseUrl + "/cust1", Customer.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("cust1", response.getBody().getCustomerId());
//    }
//
//    @Test
//    void getCustomerById_ShouldReturnNotFound_WhenIdDoesNotExist() {
//        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/cust1", String.class);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void updateCustomer_ShouldReturnUpdatedCustomer() throws Exception {
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(baseUrl, customer, Customer.class);
//
//        Customer updatedCustomer = new Customer("cust1", "Jean", "Dupont Updated");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(updatedCustomer), headers);
//
//        ResponseEntity<Customer> response = restTemplate.exchange(
//                baseUrl + "/cust1", HttpMethod.PUT, request, Customer.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Dupont Updated", response.getBody().getLastName());
//    }
//
//    @Test
//    void deleteCustomer_ShouldReturnNoContent() {
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(baseUrl, customer, Customer.class);
//
//        ResponseEntity<Void> response = restTemplate.exchange(
//                baseUrl + "/cust1", HttpMethod.DELETE, null, Void.class);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }
//
//    @Test
//    void findByNamePartial_ShouldReturnMatchingCustomers() {
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(baseUrl, customer, Customer.class);
//
//        ResponseEntity<Customer[]> response = restTemplate.getForEntity(
//                baseUrl + "/search?name=jean", Customer[].class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().length);
//        assertEquals("Jean", response.getBody()[0].getFirstName());
//    }
//
//    @Test
//    void findCustomersWithMinSpending_ShouldReturnEmpty_WhenNoOrders() {
//        Customer customer = new Customer("cust1", "Jean", "Dupont");
//        restTemplate.postForEntity(baseUrl, customer, Customer.class);
//
//        ResponseEntity<Customer[]> response = restTemplate.getForEntity(
//                baseUrl + "/min-spending?minSpending=1000", Customer[].class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(0, response.getBody().length);
//    }
//}