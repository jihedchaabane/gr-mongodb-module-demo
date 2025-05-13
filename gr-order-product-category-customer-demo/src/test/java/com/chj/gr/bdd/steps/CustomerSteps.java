package com.chj.gr.bdd.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Customer;

import io.cucumber.java.en.Given;

@ActiveProfiles("test")
public class CustomerSteps {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Given("the customer collection is empty")
    public void theCustomerCollectionIsEmpty() {
        mongoTemplate.dropCollection(Customer.class);
    }

    @Given("a customer exists with ID {string}, first name {string}, and last name {string}")
    public void aCustomerExistsWithIdFirstNameAndLastName(String id, String firstName, String lastName) {
        Customer customer = new Customer(id, firstName, lastName);
        mongoTemplate.save(customer);
    }
}