package com.chj.gr.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	
	@Id
	@NotBlank(message = "Product ID is required")
	private String id;

	@NotBlank(message = "Product name is required")
	private String name;

	@PositiveOrZero(message = "Price must be positive or zero")
	private double price;

	@PositiveOrZero(message = "Stock quantity must be positive or zero")
	private int stockQuantity;

	@NotNull(message = "Category is required")
	private Category category;
}
