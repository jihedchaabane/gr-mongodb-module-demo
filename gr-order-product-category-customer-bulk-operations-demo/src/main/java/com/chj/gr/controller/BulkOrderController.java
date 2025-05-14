package com.chj.gr.controller;

import com.chj.gr.model.Order;
import com.chj.gr.service.BulkOrderService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/bulk-orders")
public class BulkOrderController {
    private static final Logger logger = LoggerFactory.getLogger(BulkOrderController.class);
    private final BulkOrderService bulkOrderService;

    public BulkOrderController(BulkOrderService bulkOrderService) {
        this.bulkOrderService = bulkOrderService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> saveBulkOrders(@Valid @RequestBody List<Order> orders) {
        logger.info("Received request to save {} orders", orders.size());
        return bulkOrderService.saveOrdersAsync(orders)
                .thenApply(count -> {
                    String message = String.format("Successfully saved %d orders", count);
                    logger.info(message);
                    return ResponseEntity.ok(message);
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to save orders", throwable);
                    return ResponseEntity.badRequest().body("Failed to save orders: " + throwable.getMessage());
                });
    }
}