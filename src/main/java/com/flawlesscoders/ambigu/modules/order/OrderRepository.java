package com.flawlesscoders.ambigu.modules.order;

import java.util.List;
import java.util.Map;
import java.util.Date;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.flawlesscoders.ambigu.modules.order.dto.DailyOrderCount;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>{
    @Query("{ 'finalized': false, 'table': ?0, 'deleted':false}")
    Order getCurrentOrder(String tableId);

    //Que sean finalizadas por el mesero y en el workplan activo
    @Query("{ 'finalized': true, 'table': ?0, 'deleted':false, 'waiter':?1, 'workplan':?2}")
    List<Order> getFinalizedOrders(String tableId, String waiter, String workplan);

    @Query("{'table': ?tableId}")
    List<Order> getOrdersByTable(String tableId);

    @Query("{'orderNumber': ?0}")
    Order findByOrderNumber(long orderNumber);

    //FindByDateBetween
    List<Order> findByDateBetween(java.util.Date from, java.util.Date to);

    //CountByDate
    long countByDate(Date date);

    //Find by date between and deleted false
    List<Order> findByDateBetweenAndDeletedFalse(Date from, Date to);

    @Aggregation(pipeline = {
  "{ $match: { date: { $gte: ?0, $lte: ?1 }, deleted: false } }",
  "{ $group: { _id: { $dateToString: { format: '%Y-%m-%d', date: '$date' } }, count: { $sum: 1 } } }",
  "{ $sort: { _id: 1 } }"
    })
    List<DailyOrderCount> countOrdersByDay(Date from, Date to);

}