package com.chj.gr.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CommonBulkOrderSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> response;

    @When("I send a {word} request to {string}")
    public void iSendARequestTo(String method, String url) {
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        response = restTemplate.exchange(url, httpMethod, null, String.class);
    }

    @When("I send a {word} request to {string} with body:")
    public void iSendARequestToWithBody(String method, String url, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        response = restTemplate.exchange(url, httpMethod, request, String.class);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertEquals(statusCode, response.getStatusCode().value());
    }

    @Then("the response body should contain:")
    public void theResponseBodyShouldContain(String expectedBody) throws Exception {
        String actualBody = response.getBody();
//        "Successfully saved %d orders"
        
//        Object expectedJson = objectMapper.readValue(expectedBody, Object.class);
//        Object actualJson = objectMapper.readValue(actualBody, Object.class);
        
        assertEquals(expectedBody, actualBody);
    }

//    @Then("the response body should contain an array with {int} element")
//    public void theResponseBodyShouldContainAnArrayWithElement(int size) throws Exception {
//        String actualBody = response.getBody();
//        List<?> list = objectMapper.readValue(actualBody, List.class);
//        assertEquals(size, list.size());
//    }
}