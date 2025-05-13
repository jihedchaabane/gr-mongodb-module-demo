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
import com.chj.gr.model.Product;
import com.chj.gr.repository.ProductRepository;
import com.chj.gr.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("prod1", result.getId());
        assertEquals("Smartphone", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenIdExists() {
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById("prod1");

        assertTrue(result.isPresent());
        assertEquals("prod1", result.get().getId());
        verify(productRepository, times(1)).findById("prod1");
    }

    @Test
    void getProductById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        when(productRepository.findById("prod1")).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById("prod1");

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById("prod1");
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(
            new Product("prod1", "Smartphone", 599.99, 50, category),
            new Product("prod2", "Laptop", 999.99, 30, category)
        );
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Smartphone", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone Updated", 599.99, 50, category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProduct("prod1", product);

        assertNotNull(result);
        assertEquals("prod1", result.getId());
        assertEquals("Smartphone Updated", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct_ShouldCallDeleteById() {
        doNothing().when(productRepository).deleteById("prod1");

        productService.deleteProduct("prod1");

        verify(productRepository, times(1)).deleteById("prod1");
    }

    @Test
    void findProductsByCategoryId_ShouldReturnMatchingProducts() {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category));
        when(productRepository.findByCategoryCategoryId("cat1")).thenReturn(products);

        List<Product> result = productService.findProductsByCategoryId("cat1");

        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getName());
        verify(productRepository, times(1)).findByCategoryCategoryId("cat1");
    }

    @Test
    void findByPriceRange_ShouldReturnMatchingProducts() {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category));
        when(productRepository.findByPriceRange(500.0, 1000.0)).thenReturn(products);

        List<Product> result = productService.findByPriceRange(500.0, 1000.0);

        assertEquals(1, result.size());
        assertEquals(599.99, result.get(0).getPrice());
        verify(productRepository, times(1)).findByPriceRange(500.0, 1000.0);
    }

    @Test
    void findOutOfStock_ShouldReturnOutOfStockProducts() {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 0, category));
        when(productRepository.findOutOfStock()).thenReturn(products);

        List<Product> result = productService.findOutOfStock();

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStockQuantity());
        verify(productRepository, times(1)).findOutOfStock();
    }

    @Test
    void findTopExpensiveByCategory_ShouldReturnTopProducts() {
        Category category = new Category("cat1", "Electronics");
        List<Product> products = Arrays.asList(new Product("prod1", "Smartphone", 599.99, 50, category));
        when(productRepository.findTopExpensiveByCategory("cat1", 5)).thenReturn(products);

        List<Product> result = productService.findTopExpensiveByCategory("cat1", 5);

        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getName());
        verify(productRepository, times(1)).findTopExpensiveByCategory("cat1", 5);
    }
}