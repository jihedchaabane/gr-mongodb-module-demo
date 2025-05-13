package com.chj.gr.mocks.controllers.exceptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class ProductControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        when(productService.createProduct(any(Product.class))).thenThrow(new RuntimeException("Database error"));
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(productService.updateProduct(eq("prod1"), any(Product.class)))
                .thenThrow(new IllegalArgumentException("Invalid product ID"));
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);

        mockMvc.perform(put("/api/products/prod1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).updateProduct(eq("prod1"), any(Product.class));
    }

    @Test
    void deleteProduct_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        doThrow(new RuntimeException("Database error")).when(productService).deleteProduct("prod1");

        mockMvc.perform(delete("/api/products/prod1"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1)).deleteProduct("prod1");
    }

    @Test
    void findByPriceRange_ShouldReturnBadRequest_WhenMinPriceIsGreaterThanMaxPrice() throws Exception {
        when(productService.findByPriceRange(1000.0, 500.0))
                .thenThrow(new IllegalArgumentException("minPrice cannot be greater than maxPrice"));

        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "1000")
                .param("maxPrice", "500"))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).findByPriceRange(1000.0, 500.0);
    }

    @Test
    void findTopExpensiveByCategory_ShouldReturnBadRequest_WhenLimitIsNegative() throws Exception {
        when(productService.findTopExpensiveByCategory("cat1", -1))
                .thenThrow(new IllegalArgumentException("Limit cannot be negative"));

        mockMvc.perform(get("/api/products/top-expensive/cat1").param("limit", "-1"))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).findTopExpensiveByCategory("cat1", -1);
    }
}