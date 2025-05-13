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

import com.chj.gr.controller.CategoryController;
import com.chj.gr.model.Category;
import com.chj.gr.service.CategoryService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @Disabled
    void createCategory_ShouldReturnBadRequest_WhenCategoryIdIsMissing() throws Exception {
        String invalidJson = "{\"categoryName\": \"Electronics\"}"; // categoryId manquant
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void createCategory_ShouldReturnBadRequest_WhenCategoryNameIsEmpty() throws Exception {
        String invalidJson = "{\"categoryId\": \"cat1\", \"categoryName\": \"\"}";
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void updateCategory_ShouldReturnBadRequest_WhenCategoryNameIsMissing() throws Exception {
        String invalidJson = "{\"categoryId\": \"cat1\"}";
        mockMvc.perform(put("/api/categories/cat1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "electro, 1",  // Recherche normale
            ", 0",         // Nom vide
            "xyz, 0"       // Nom non trouvé
    })
    void findByNamePartial_ShouldReturnCategories(String name, int expectedSize) throws Exception {
        List<Category> categories = expectedSize > 0 ? Arrays.asList(new Category("cat1", "Electronics")) : Collections.emptyList();
        when(categoryService.findByNamePartial(name == null ? "" : name)).thenReturn(categories);

        mockMvc.perform(get("/api/categories/search").param("name", name == null ? "" : name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(categoryService, times(1)).findByNamePartial(name == null ? "" : name);
    }

    @ParameterizedTest
    @CsvSource({
            "5, 1",   // Nombre minimum normal
            "0, 1",   // Zéro (toutes catégories)
            "-1, 0"   // Valeur négative
    })
    void findCategoriesWithMinProducts_ShouldReturnCategories(int minProducts, int expectedSize) throws Exception {
        List<Category> categories = expectedSize > 0 ? Arrays.asList(new Category("cat1", "Electronics")) : Collections.emptyList();
        when(categoryService.findCategoriesWithMinProducts(minProducts)).thenReturn(categories);

        mockMvc.perform(get("/api/categories/min-products").param("minProducts", String.valueOf(minProducts)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(categoryService, times(1)).findCategoriesWithMinProducts(minProducts);
    }
}