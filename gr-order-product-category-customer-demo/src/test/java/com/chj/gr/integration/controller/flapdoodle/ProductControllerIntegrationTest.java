//package com.chj.gr.integration.controller.flapdoodle;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import com.chj.gr.model.Category;
//import com.chj.gr.model.Product;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Disabled
//public class ProductControllerIntegrationTest {
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
//    private String categoryUrl;
//
//    @BeforeEach
//    void setUp() {
//        baseUrl = "http://localhost:" + port + "/api/products";
//        categoryUrl = "http://localhost:" + port + "/api/categories";
//        restTemplate.delete(baseUrl);
//        restTemplate.delete(categoryUrl);
//    }
//
//    @Test
//    void createProduct_ShouldReturnCreatedProduct() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(product), headers);
//
//        ResponseEntity<Product> response = restTemplate.postForEntity(baseUrl, request, Product.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("prod1", response.getBody().getId());
//        assertEquals("Smartphone", response.getBody().getName());
//    }
//
//    @Test
//    void createProduct_ShouldReturnBadRequest_WhenPriceIsNegative() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        String invalidJson = "{\"id\": \"prod1\", \"name\": \"Smartphone\", \"price\": -100, \"stockQuantity\": 50, \"category\": " + objectMapper.writeValueAsString(category) + "}";
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
//    void getProductById_ShouldReturnProduct_WhenIdExists() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(baseUrl, product, Product.class);
//
//        ResponseEntity<Product> response = restTemplate.getForEntity(baseUrl + "/prod1", Product.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("prod1", response.getBody().getId());
//    }
//
//    @Test
//    void getProductById_ShouldReturnNotFound_WhenIdDoesNotExist() {
//        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/prod1", String.class);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(baseUrl, product, Product.class);
//
//        Product updatedProduct = new Product("prod1", "Smartphone Updated", 699.99, 40, category);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(updatedProduct), headers);
//
//        ResponseEntity<Product> response = restTemplate.exchange(
//                baseUrl + "/prod1", HttpMethod.PUT, request, Product.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Smartphone Updated", response.getBody().getName());
//    }
//
//    @Test
//    void deleteProduct_ShouldReturnNoContent() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(baseUrl, product, Product.class);
//
//        ResponseEntity<Void> response = restTemplate.exchange(
//                baseUrl + "/prod1", HttpMethod.DELETE, null, Void.class);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }
//
//    @Test
//    void findByPriceRange_ShouldReturnMatchingProducts() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(baseUrl, product, Product.class);
//
//        ResponseEntity<Product[]> response = restTemplate.getForEntity(
//                baseUrl + "/price-range?minPrice=500&maxPrice=1000", Product[].class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().length);
//        assertEquals("prod1", response.getBody()[0].getId());
//    }
//
//    @Test
//    void findOutOfStock_ShouldReturnEmpty_WhenNoProductsOutOfStock() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(categoryUrl, category, Category.class);
//
//        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
//        restTemplate.postForEntity(baseUrl, product, Product.class);
//
//        ResponseEntity<Product[]> response = restTemplate.getForEntity(
//                baseUrl + "/out-of-stock", Product[].class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(0, response.getBody().length);
//    }
//}