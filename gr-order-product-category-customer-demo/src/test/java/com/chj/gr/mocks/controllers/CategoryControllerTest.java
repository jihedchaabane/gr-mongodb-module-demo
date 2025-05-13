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

import com.chj.gr.controller.CategoryController;
import com.chj.gr.model.Category;
import com.chj.gr.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        Category category = new Category("cat1", "Electronics");
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value("cat1"))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenIdExists() throws Exception {
        Category category = new Category("cat1", "Electronics");
        when(categoryService.getCategoryById("cat1")).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/categories/cat1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value("cat1"))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        verify(categoryService, times(1)).getCategoryById("cat1");
    }

    @Test
    void getCategoryById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        when(categoryService.getCategoryById("cat1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/cat1"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById("cat1");
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        List<Category> categories = Arrays.asList(
                new Category("cat1", "Electronics"),
                new Category("cat2", "Books")
        );
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value("cat1"))
                .andExpect(jsonPath("$[1].categoryName").value("Books"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        Category category = new Category("cat1", "Electronics Updated");
        when(categoryService.updateCategory(eq("cat1"), any(Category.class))).thenReturn(category);

        mockMvc.perform(put("/api/categories/cat1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value("cat1"))
                .andExpect(jsonPath("$.categoryName").value("Electronics Updated"));

        verify(categoryService, times(1)).updateCategory(eq("cat1"), any(Category.class));
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory("cat1");

        mockMvc.perform(delete("/api/categories/cat1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory("cat1");
    }

    @Test
    void findByNamePartial_ShouldReturnMatchingCategories() throws Exception {
        List<Category> categories = Arrays.asList(new Category("cat1", "Electronics"));
        when(categoryService.findByNamePartial("electro")).thenReturn(categories);

        mockMvc.perform(get("/api/categories/search?name=electro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"));

        verify(categoryService, times(1)).findByNamePartial("electro");
    }

    @Test
    void countProductsByCategory_ShouldReturnCategoriesWithCounts() throws Exception {
        List<Category> categories = Arrays.asList(new Category("cat1", "Electronics"));
        when(categoryService.countProductsByCategory()).thenReturn(categories);

        mockMvc.perform(get("/api/categories/product-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"));

        verify(categoryService, times(1)).countProductsByCategory();
    }

    @Test
    void findCategoriesWithMinProducts_ShouldReturnMatchingCategories() throws Exception {
        List<Category> categories = Arrays.asList(new Category("cat1", "Electronics"));
        when(categoryService.findCategoriesWithMinProducts(5)).thenReturn(categories);

        mockMvc.perform(get("/api/categories/min-products?minProducts=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"));

        verify(categoryService, times(1)).findCategoriesWithMinProducts(5);
    }
}