package com.chj.gr.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.chj.gr.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
	
	@Query("{ 'categoryName': { $regex: ?0, $options: 'i' } }")
    List<Category> findByNamePartial(String name);

    @Query(value = "[{ $lookup: { from: 'products', let: { catId: '$categoryId' }, pipeline: [ { $match: { 'category.categoryId': { $eq: '$$catId' } } } ], as: 'products' } }, { $project: { categoryId: 1, categoryName: 1, productCount: { $size: '$products' } } }]")
    List<Category> countProductsByCategory();

    @Query(value = "[{ $lookup: { from: 'products', let: { catId: '$categoryId' }, pipeline: [ { $match: { 'category.categoryId': { $eq: '$$catId' } } } ], as: 'products' } }, { $match: { 'products': { $gte: { $size: ?0 } } } }]")
    List<Category> findCategoriesWithMinProducts(int minProducts);
}