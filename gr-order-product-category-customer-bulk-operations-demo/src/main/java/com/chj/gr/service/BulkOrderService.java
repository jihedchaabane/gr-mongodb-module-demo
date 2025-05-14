package com.chj.gr.service;

import com.chj.gr.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class BulkOrderService {
    private static final Logger logger = LoggerFactory.getLogger(BulkOrderService.class);
    private final MongoTemplate mongoTemplate;
    private final LocalValidatorFactoryBean validator;
    private static final int BATCH_SIZE = 10_000;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    public BulkOrderService(MongoTemplate mongoTemplate, LocalValidatorFactoryBean validator) {
        this.mongoTemplate = mongoTemplate;
        this.validator = validator;
    }

    public CompletableFuture<Long> saveOrdersAsync(List<Order> orders) {
        logger.info("Starting async bulk save of {} orders", orders.size());
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            try {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                int totalOrders = orders.size();

                for (int i = 0; i < totalOrders; i += BATCH_SIZE) {
                    int endIndex = Math.min(i + BATCH_SIZE, totalOrders);
                    List<Order> batch = orders.subList(i, endIndex);
                    futures.add(CompletableFuture.runAsync(() -> saveBatch(batch), executor));
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Completed bulk save of {} orders in {} ms", totalOrders, duration);
                return (long) totalOrders;
            } catch (Exception e) {
                logger.error("Error during bulk save", e);
                throw new RuntimeException("Failed to save orders: " + e.getMessage(), e);
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        logger.warn("Executor did not terminate within 60 seconds, forcing shutdown");
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    logger.error("Executor shutdown interrupted", e);
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void saveBatch(List<Order> batch) {
        for (Order order : batch) {
            Set<ConstraintViolation<Order>> violations = validator.validate(order);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Order.class);
        bulkOps.insert(batch);
        try {
            bulkOps.execute();
            logger.debug("Saved batch of {} orders", batch.size());
        } catch (Exception e) {
            logger.error("Failed to save batch of {} orders", batch.size(), e);
            throw new RuntimeException("Batch save failed: " + e.getMessage(), e);
        }
    }
}