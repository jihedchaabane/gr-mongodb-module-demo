package com.chj.gr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.chj.gr.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
    
	@Query("{ 'orderDate': { $gte: ?0, $lte: ?1 } }")
    List<Order> findByDateRange(Date startDate, Date endDate);

    @Query("{ 'status': ?0 }")
    List<Order> findByStatus(String status);

    @Query(value = "[{ $match: { 'customer.customerId': ?0 } }, { $unwind: '$products' }, { $group: { _id: '$customer.customerId', totalAmount: { $sum: '$products.price' } } }]")
    List<TotalAmount> findTotalAmountByCustomer(String customerId);

    interface TotalAmount {
        String getId();
        double getTotalAmount();
    }
}