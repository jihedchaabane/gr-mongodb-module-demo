package com.chj.gr.integration.controller.real;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Category;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CategoryControllerRealDbIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/categories";
        mongoTemplate.dropCollection(Category.class);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(Category.class);
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        Category category = new Category("cat1", "Electronics");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(category), headers);

        ResponseEntity<Category> response = restTemplate.postForEntity(baseUrl, request, Category.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("cat1", response.getBody().getCategoryId());
        assertEquals("Electronics", response.getBody().getCategoryName());
    }

    @Test
    void createCategory_ShouldReturnBadRequest_WhenCategoryIdIsMissing() throws Exception {
        String invalidJson = "{\"categoryName\": \"Electronics\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenIdExists() {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        ResponseEntity<Category> response = restTemplate.getForEntity(baseUrl + "/cat1", Category.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("cat1", response.getBody().getCategoryId());
    }

    @Test
    void getCategoryById_ShouldReturnNotFound_WhenIdDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/cat1", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        Category updatedCategory = new Category("cat1", "Electronics Updated");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(updatedCategory), headers);

        ResponseEntity<Category> response = restTemplate.exchange(
                baseUrl + "/cat1", HttpMethod.PUT, request, Category.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Electronics Updated", response.getBody().getCategoryName());
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/cat1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void findByNamePartial_ShouldReturnMatchingCategories() {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        ResponseEntity<Category[]> response = restTemplate.getForEntity(
                baseUrl + "/search?name=electro", Category[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Electronics", response.getBody()[0].getCategoryName());
    }

    @Test
    @Disabled
    void findCategoriesWithMinProducts_ShouldReturnEmpty_WhenNoProducts() {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        ResponseEntity<Category[]> response = restTemplate.getForEntity(
                baseUrl + "/min-products?minProducts=1", Category[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }
}