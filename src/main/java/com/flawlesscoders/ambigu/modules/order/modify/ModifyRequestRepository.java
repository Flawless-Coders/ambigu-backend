package com.flawlesscoders.ambigu.modules.order.modify;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifyRequestRepository extends MongoRepository<ModifyRequest, String> {
    @Aggregation(pipeline = {
        "{ '$match': { 'deletedRequest': false } }",
        "{ '$lookup': { " +
        "   'from': 'orders', " +
        "   'let': { 'orderId': '$orderId' }, " +
        "   'pipeline': [ " +
        "       { '$match': { " +
        "           '$expr': { " +
        "               '$and': [ " +
        "                   { '$eq': ['$_id', { '$toObjectId': '$$orderId' }] }, " +
        "                   { '$eq': ['$deleted', false] }, " +
        "                   { '$eq': ['$finalized', false] } " +
        "               ] " +
        "           } " +
        "       }} " +
        "   ], " +
        "   'as': 'orderDetails' " +
        "} }",
        "{ '$match': { 'orderDetails': { '$ne': [] } } }",
        "{ '$project': { " +
        "   'deletedRequest': 1, " +
        "   'modifiedDishes': 1, " +
        "   'orderId': 1, " +
        "   'orderNumber': 1, " +
        "   'table': 1, " +
        "   'toDelete': 1, " +
        "   'total': 1, " +
        "   'waiter': 1 " +
        "} }"
    })
    List<ModifyRequest> findActiveModifyRequestsWithOrders();
}
