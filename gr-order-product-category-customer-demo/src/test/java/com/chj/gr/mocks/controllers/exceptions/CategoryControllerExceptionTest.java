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

import com.chj.gr.controller.CategoryController;
import com.chj.gr.model.Category;
import com.chj.gr.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        when(categoryService.createCategory(any(Category.class))).thenThrow(new RuntimeException("Database error"));
        Category category = new Category("cat1", "Electronics");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isInternalServerError());

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    void updateCategory_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(categoryService.updateCategory(eq("cat1"), any(Category.class)))
                .thenThrow(new IllegalArgumentException("Invalid category ID"));
        Category category = new Category("cat1", "Electronics");

        mockMvc.perform(put("/api/categories/cat1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).updateCategory(eq("cat1"), any(Category.class));
    }

    @Test
    void deleteCategory_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        doThrow(new RuntimeException("Database error")).when(categoryService).deleteCategory("cat1");

        mockMvc.perform(delete("/api/categories/cat1"))
                .andExpect(status().isInternalServerError());

        verify(categoryService, times(1)).deleteCategory("cat1");
    }

    @Test
    void findByNamePartial_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(categoryService.findByNamePartial("invalid"))
                .thenThrow(new IllegalArgumentException("Invalid search term"));

        mockMvc.perform(get("/api/categories/search").param("name", "invalid"))
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).findByNamePartial("invalid");
    }

    @Test
    void findCategoriesWithMinProducts_ShouldReturnBadRequest_WhenMinProductsIsNegative() throws Exception {
        when(categoryService.findCategoriesWithMinProducts(-1))
                .thenThrow(new IllegalArgumentException("minProducts cannot be negative"));

        mockMvc.perform(get("/api/categories/min-products").param("minProducts", "-1"))
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).findCategoriesWithMinProducts(-1);
    }
}