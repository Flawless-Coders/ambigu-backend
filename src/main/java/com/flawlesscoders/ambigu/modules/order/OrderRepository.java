package com.flawlesscoders.ambigu.modules.order;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>{
    @Query("{ 'finalized': false, 'table': ?0, 'deleted':false}")
    Order getCurrentOrder(String tableIdentifier);

    @Query("{ 'finalized': true, 'waiter':?0  }")
    List<Order> getFinalizedOrders(String waiterName);

    @Query("{'table': ?tableId}")
    List<Order> getOrdersByTable(String tableId);

}
