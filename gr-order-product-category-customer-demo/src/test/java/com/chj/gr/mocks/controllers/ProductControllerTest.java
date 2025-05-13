package com.chj.gr.mocks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chj.gr.controller.ProductController;
import com.chj.gr.model.Category;
import com.chj.gr.model.Product;
import com.chj.gr.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod1"))
                .andExpect(jsonPath("$.name").value("Smartphone"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenIdExists() throws Exception {
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        when(productService.getProductById("prod1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod1"))
                .andExpect(jsonPath("$.name").value("Smartphone"));

        verify(productService, times(1)).getProductById("prod1");
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        when(productService.getProductById("prod1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/prod1"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById("prod1");
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(
                new Product("prod1", "Smartphone", 599.99, 50, category),
                new Product("prod2", "Laptop", 999.99, 30, category)
        );
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("prod1"))
                .andExpect(jsonPath("$[1].name").value("Laptop"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone Updated", 599.99, 50, category);
        when(productService.updateProduct(eq("prod1"), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/prod1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod1"))
                .andExpect(jsonPath("$.name").value("Smartphone Updated"));

        verify(productService, times(1)).updateProduct(eq("prod1"), any(Product.class));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct("prod1");

        mockMvc.perform(delete("/api/products/prod1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct("prod1");
    }

    @Test
    void findProductsByCategoryId_ShouldReturnMatchingProducts() throws Exception {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category));
        when(productService.findProductsByCategoryId("cat1")).thenReturn(products);

        mockMvc.perform(get("/api/products/category/cat1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("prod1"));

        verify(productService, times(1)).findProductsByCategoryId("cat1");
    }

    @Test
    void findByPriceRange_ShouldReturnMatchingProducts() throws Exception {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category));
        when(productService.findByPriceRange(500.0, 1000.0)).thenReturn(products);

        mockMvc.perform(get("/api/products/price-range?minPrice=500&maxPrice=1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("prod1"));

        verify(productService, times(1)).findByPriceRange(500.0, 1000.0);
    }

    @Test
    void findOutOfStock_ShouldReturnOutOfStockProducts() throws Exception {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 0, category));
        when(productService.findOutOfStock()).thenReturn(products);

        mockMvc.perform(get("/api/products/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("prod1"));

        verify(productService, times(1)).findOutOfStock();
    }

    @Test
    void findTopExpensiveByCategory_ShouldReturnTopProducts() throws Exception {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category));
        when(productService.findTopExpensiveByCategory("cat1", 5)).thenReturn(products);

        mockMvc.perform(get("/api/products/top-expensive/cat1?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("prod1"));

        verify(productService, times(1)).findTopExpensiveByCategory("cat1", 5);
    }
}