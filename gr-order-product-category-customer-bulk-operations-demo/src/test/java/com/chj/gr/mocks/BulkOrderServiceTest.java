package com.chj.gr.mocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.chj.gr.model.Order;
import com.chj.gr.service.BulkOrderService;
import com.chj.gr.util.OrderGenerator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class BulkOrderServiceTest {
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private LocalValidatorFactoryBean validator;
    @Mock
    private BulkOperations bulkOperations;
    @InjectMocks
    private BulkOrderService bulkOrderService;

    private List<Order> validOrders;

    @BeforeEach
    void setUp() {
        validOrders = OrderGenerator.generateOrders(1);
        when(mongoTemplate.bulkOps(eq(BulkOperations.BulkMode.UNORDERED), eq(Order.class))).thenReturn(bulkOperations);
        when(bulkOperations.insert(any())).thenReturn(bulkOperations);
        when(bulkOperations.execute()).thenReturn(null);
        when(validator.validate(any(Order.class))).thenReturn(Set.of());
    }

    @Test
    void saveOrdersAsync_Success() {
        bulkOrderService.saveOrdersAsync(validOrders)
                .thenAccept(count -> assertEquals(1, count))
                .join();
        verify(bulkOperations).insert(validOrders);
        verify(bulkOperations).execute();
    }

    @Test
    void saveOrdersAsync_ValidationFailure() {
        when(validator.validate(any(Order.class))).thenThrow(new CompletionException(new Exception("")));
        assertThrows(CompletionException.class, () -> bulkOrderService.saveOrdersAsync(validOrders).join());
        verify(bulkOperations, never()).execute();
    }

    @Test
    void saveLargeBatchOrders_Success() {
        List<Order> largeBatch = OrderGenerator.generateOrders(1000);
        bulkOrderService.saveOrdersAsync(largeBatch)
                .thenAccept(count -> assertEquals(1000, count))
                .join();
        verify(bulkOperations, atLeastOnce()).insert(any());
        verify(bulkOperations, atLeastOnce()).execute();
    }
}