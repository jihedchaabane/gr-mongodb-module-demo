package com.chj.gr.controller;

import com.chj.gr.model.Product;
import com.chj.gr.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @Valid @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> findProductsByCategoryId(@PathVariable String categoryId) {
        return productService.findProductsByCategoryId(categoryId);
    }

    @GetMapping("/price-range")
    public List<Product> findByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice) {
        return productService.findByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/out-of-stock")
    public List<Product> findOutOfStock() {
        return productService.findOutOfStock();
    }

    @GetMapping("/top-expensive/{categoryId}")
    public List<Product> findTopExpensiveByCategory(@PathVariable String categoryId, @RequestParam int limit) {
        return productService.findTopExpensiveByCategory(categoryId, limit);
    }
}