package com.chj.gr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chj.gr.model.Category;
import com.chj.gr.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(String id, Category category) {
        category.setCategoryId(id);
        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> findByNamePartial(String name) {
        return categoryRepository.findByNamePartial(name);
    }

    public List<Category> countProductsByCategory() {
        return categoryRepository.countProductsByCategory();
    }

    public List<Category> findCategoriesWithMinProducts(int minProducts) {
        return categoryRepository.findCategoriesWithMinProducts(minProducts);
    }
}