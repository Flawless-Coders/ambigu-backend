package com.flawlesscoders.ambigu.modules.order;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>{
    @Query("{ 'finalized': false, 'table': ?0, 'deleted':false}")
    Order getCurrentOrder(String tableId);

    //Método que trae todas las mesas
    @Query("{ 'finalized': true, 'table': ?0, 'deleted':false}")
    List<Order> getFinalizedOrders(String tableId);

    @Query("{'table': ?tableId}")
    List<Order> getOrdersByTable(String tableId);

    @Query("{'orderNumber': ?0}")
    Order findByOrderNumber(long orderNumber);

}
