package com.chj.gr.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.chj.gr.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
	
    List<Product> findByCategoryCategoryId(String categoryId);

    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(double minPrice, double maxPrice);

    @Query("{ 'stockQuantity': 0 }")
    List<Product> findOutOfStock();

    @Query(value = "[{ $match: { 'category.categoryId': ?0 } }, { $sort: { price: -1 } }, { $limit: ?1 }]")
    List<Product> findTopExpensiveByCategory(String categoryId, int limit);
}