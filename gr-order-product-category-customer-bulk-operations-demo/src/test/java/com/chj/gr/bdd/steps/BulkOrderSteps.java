package com.chj.gr.bdd.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Order;

import io.cucumber.java.en.Given;

@ActiveProfiles("test")
public class BulkOrderSteps {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Given("the order collection is empty")
    public void theOrderCollectionIsEmpty() {
        mongoTemplate.dropCollection(Order.class);
    }

}