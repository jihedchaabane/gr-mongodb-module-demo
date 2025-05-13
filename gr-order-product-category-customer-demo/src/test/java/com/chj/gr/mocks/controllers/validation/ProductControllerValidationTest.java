package com.chj.gr.mocks.controllers.validation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @Disabled
    void createProduct_ShouldReturnBadRequest_WhenPriceIsNegative() throws Exception {
        String invalidJson = "{\"id\": \"prod1\", \"name\": \"Smartphone\", \"price\": -100, \"stockQuantity\": 50, \"category\": {\"categoryId\": \"cat1\", \"categoryName\": \"Electronics\"}}";
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void createProduct_ShouldReturnBadRequest_WhenCategoryIsMissing() throws Exception {
        String invalidJson = "{\"id\": \"prod1\", \"name\": \"Smartphone\", \"price\": 599.99, \"stockQuantity\": 50}";
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void updateProduct_ShouldReturnBadRequest_WhenStockQuantityIsNegative() throws Exception {
        String invalidJson = "{\"id\": \"prod1\", \"name\": \"Smartphone\", \"price\": 599.99, \"stockQuantity\": -10, \"category\": {\"categoryId\": \"cat1\", \"categoryName\": \"Electronics\"}}";
        mockMvc.perform(put("/api/products/prod1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "500, 1000, 1",  // Plage normale
            "0, 100, 1",     // Plage avec minPrice=0
            "-1, 100, 0"     // Plage avec minPrice négatif
    })
    void findByPriceRange_ShouldReturnProducts(double minPrice, double maxPrice, int expectedSize) throws Exception {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = expectedSize > 0
        				? Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category))
        				: Collections.emptyList();
        when(productService.findByPriceRange(minPrice, maxPrice)).thenReturn(products);

        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", String.valueOf(minPrice))
                .param("maxPrice", String.valueOf(maxPrice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(productService, times(1)).findByPriceRange(minPrice, maxPrice);
    }

    @ParameterizedTest
    @CsvSource({
            "cat1, 5, 1",   // Limite normale
            "cat1, 0, 0",   // Zéro
            "cat1, -1, 0"   // Valeur négative
    })
    void findTopExpensiveByCategory_ShouldReturnProducts(String categoryId, int limit, int expectedSize) throws Exception {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = expectedSize > 0 ? Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category)) : Collections.emptyList();
        when(productService.findTopExpensiveByCategory(categoryId, limit)).thenReturn(products);

        mockMvc.perform(get("/api/products/top-expensive/" + categoryId)
                .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(productService, times(1)).findTopExpensiveByCategory(categoryId, limit);
    }
}