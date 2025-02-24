package com.flawlesscoders.ambigu.modules.order;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>{
    @Query("{ 'finalized': false }")
    List<Order> getCurrentOrders();

    @Query("{ 'finalized': true }")
    List<Order> getFinalizedOrders();

    @Query("{'table': ?tableId}")
    List<Order> getOrdersByTable(String tableId);

}
