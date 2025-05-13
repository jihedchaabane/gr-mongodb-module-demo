package com.chj.gr.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	
    @Id
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Order date is required")
//    @PastOrPresent(message = "Order date must be in the past or present")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "orderDate must be in format yyyy-MM-dd HH:mm:ss")
    private String orderDate;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Customer is required")
    private Customer customer;

    @NotNull(message = "Products list is required")
    private List<Product> products;

    public void addProduct(Product product) {
        if (product != null) {
        	if (products == null) {
        		products = new ArrayList<>();
			}
            this.products.add(product);
        }
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }
}