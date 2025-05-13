package com.chj.gr.bdd.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Category;

import io.cucumber.java.en.Given;

@ActiveProfiles("test")
public class CategorySteps {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Given("the category collection is empty")
    public void theCategoryCollectionIsEmpty() {
        mongoTemplate.dropCollection(Category.class);
    }

    @Given("a category exists with ID {string} and name {string}")
    public void aCategoryExistsWithIdAndName(String id, String name) {
        Category category = new Category(id, name);
        mongoTemplate.save(category);
    }
}