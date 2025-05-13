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
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Disabled
//public class CategoryControllerIntegrationTest {
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
//        baseUrl = "http://localhost:" + port + "/api/categories";
//        // Clear database before each test
//        restTemplate.delete(baseUrl);
//    }
//
//    @Test
//    void createCategory_ShouldReturnCreatedCategory() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(category), headers);
//
//        ResponseEntity<Category> response = restTemplate.postForEntity(baseUrl, request, Category.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("cat1", response.getBody().getCategoryId());
//        assertEquals("Electronics", response.getBody().getCategoryName());
//    }
//
//    @Test
//    void createCategory_ShouldReturnBadRequest_WhenCategoryIdIsMissing() throws Exception {
//        String invalidJson = "{\"categoryName\": \"Electronics\"}";
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
//    void getCategoryById_ShouldReturnCategory_WhenIdExists() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(baseUrl, category, Category.class);
//
//        ResponseEntity<Category> response = restTemplate.getForEntity(baseUrl + "/cat1", Category.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("cat1", response.getBody().getCategoryId());
//    }
//
//    @Test
//    void getCategoryById_ShouldReturnNotFound_WhenIdDoesNotExist() {
//        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/cat1", String.class);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(baseUrl, category, Category.class);
//
//        Category updatedCategory = new Category("cat1", "Electronics Updated");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(updatedCategory), headers);
//
//        ResponseEntity<Category> response = restTemplate.exchange(
//                baseUrl + "/cat1", HttpMethod.PUT, request, Category.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Electronics Updated", response.getBody().getCategoryName());
//    }
//
//    @Test
//    void deleteCategory_ShouldReturnNoContent() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(baseUrl, category, Category.class);
//
//        ResponseEntity<Void> response = restTemplate.exchange(
//                baseUrl + "/cat1", HttpMethod.DELETE, null, Void.class);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }
//
//    @Test
//    void findByNamePartial_ShouldReturnMatchingCategories() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(baseUrl, category, Category.class);
//
//        ResponseEntity<Category[]> response = restTemplate.getForEntity(
//                baseUrl + "/search?name=electro", Category[].class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().length);
//        assertEquals("Electronics", response.getBody()[0].getCategoryName());
//    }
//
//    @Test
//    void findCategoriesWithMinProducts_ShouldReturnEmpty_WhenNoProducts() {
//        Category category = new Category("cat1", "Electronics");
//        restTemplate.postForEntity(baseUrl, category, Category.class);
//
//        ResponseEntity<Category[]> response = restTemplate.getForEntity(
//                baseUrl + "/min-products?minProducts=1", Category[].class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(0, response.getBody().length);
//    }
//}