package com.chj.gr.bdd.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Category;
import com.chj.gr.model.Product;

import io.cucumber.java.en.Given;

@ActiveProfiles("test")
public class ProductSteps {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Given("the product and category collections are empty")
    public void theProductAndCategoryCollectionsAreEmpty() {
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Category.class);
    }

    @Given("a product exists with ID {string}, name {string}, price {double}, stock {int}, and category ID {string}")
    public void aProductExistsWithIdNamePriceStockAndCategoryId(String id, String name, double price, int stock, String categoryId) {
        Category category = mongoTemplate.findById(categoryId, Category.class);
        Product product = new Product(id, name, price, stock, category);
        mongoTemplate.save(product);
    }
}