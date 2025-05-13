package com.chj.gr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chj.gr.model.Product;
import com.chj.gr.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public List<Product> findProductsByCategoryId(String categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId);
    }

    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Product> findOutOfStock() {
        return productRepository.findOutOfStock();
    }

    public List<Product> findTopExpensiveByCategory(String categoryId, int limit) {
        return productRepository.findTopExpensiveByCategory(categoryId, limit);
    }
}