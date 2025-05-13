package com.chj.gr.mocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chj.gr.model.Category;
import com.chj.gr.repository.CategoryRepository;
import com.chj.gr.service.CategoryService;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_ShouldReturnSavedCategory() {
        Category category = new Category("cat1", "Electronics");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("cat1", result.getCategoryId());
        assertEquals("Electronics", result.getCategoryName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenIdExists() {
        Category category = new Category("cat1", "Electronics");
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById("cat1");

        assertTrue(result.isPresent());
        assertEquals("cat1", result.get().getCategoryId());
        verify(categoryRepository, times(1)).findById("cat1");
    }

    @Test
    void getCategoryById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        when(categoryRepository.findById("cat1")).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById("cat1");

        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById("cat1");
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        List<Category> categories = Arrays.asList(
            new Category("cat1", "Electronics"),
            new Category("cat2", "Books")
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        Category category = new Category("cat1", "Electronics Updated");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.updateCategory("cat1", category);

        assertNotNull(result);
        assertEquals("cat1", result.getCategoryId());
        assertEquals("Electronics Updated", result.getCategoryName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void deleteCategory_ShouldCallDeleteById() {
        doNothing().when(categoryRepository).deleteById("cat1");

        categoryService.deleteCategory("cat1");

        verify(categoryRepository, times(1)).deleteById("cat1");
    }

    @Test
    void findByNamePartial_ShouldReturnMatchingCategories() {
        List<Category> categories = Arrays.asList(new Category("cat1", "Electronics"));
        when(categoryRepository.findByNamePartial("electro")).thenReturn(categories);

        List<Category> result = categoryService.findByNamePartial("electro");

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
        verify(categoryRepository, times(1)).findByNamePartial("electro");
    }

    @Test
    void findByNamePartial_ShouldReturnEmptyList_WhenNoMatch() {
        when(categoryRepository.findByNamePartial("xyz")).thenReturn(List.of());

        List<Category> result = categoryService.findByNamePartial("xyz");

        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findByNamePartial("xyz");
    }

    @Test
    void countProductsByCategory_ShouldReturnCategoriesWithCounts() {
        List<Category> categories = Arrays.asList(new Category("cat1", "Electronics"));
        when(categoryRepository.countProductsByCategory()).thenReturn(categories);

        List<Category> result = categoryService.countProductsByCategory();

        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).countProductsByCategory();
    }

    @Test
    void findCategoriesWithMinProducts_ShouldReturnMatchingCategories() {
        List<Category> categories = Arrays.asList(new Category("cat1", "Electronics"));
        when(categoryRepository.findCategoriesWithMinProducts(5)).thenReturn(categories);

        List<Category> result = categoryService.findCategoriesWithMinProducts(5);

        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findCategoriesWithMinProducts(5);
    }
}