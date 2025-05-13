package com.chj.gr.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.chj.gr.model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    @Query("{ $or: [ { 'firstName': { $regex: ?0, $options: 'i' } }, { 'lastName': { $regex: ?0, $options: 'i' } } ] }")
    List<Customer> findByNamePartial(String name);

    @Query(value = "[{ $lookup: { from: 'orders', let: { custId: '$customerId' }, pipeline: [ { $match: { 'customer.customerId': { $eq: '$$custId' } } }, { $unwind: '$products' }, { $group: { _id: null, total: { $sum: '$products.price' } } } ], as: 'orderTotals' } }, { $match: { 'orderTotals.total': { $gte: ?0 } } }]")
    List<Customer> findCustomersWithMinSpending(double minSpending);

    @Query(value = "[{ $lookup: { from: 'orders', let: { custId: '$customerId' }, pipeline: [ { $match: { 'customer.customerId': { $eq: '$$custId' } } } ], as: 'orders' } }, { $project: { customerId: 1, firstName: 1, lastName: 1, orderCount: { $size: '$orders' } } }, { $sort: { orderCount: -1 } }, { $limit: ?0 }]")
    List<Customer> findTopActiveCustomers(int limit);

    @Query(value = "[{ $lookup: { from: 'orders', let: { customerId: '$customerId' }, pipeline: [ { $match: { 'customer.customerId': { $eq: '$$customerId' } } }, { $limit: 1 } ], as: 'orders' } }, { $match: { 'orders': { $ne: [] } } }]")
    List<Customer> findCustomersWithOrders();
}