package com.flawlesscoders.ambigu.modules.order;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlesscoders.ambigu.modules.order.dto.OrderFeedbackDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService service;

    @Operation(summary = "Get all orders", description = "Returns a list of all registered orders")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(){
        return ResponseEntity.ok(service.getAllOrders());
    }

    @Operation(summary = "Get order by ID", description = "Returns the order with the specified ID")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id){
        return ResponseEntity.ok(service.getOrderById(id));
    }

    @Operation(summary = "Create order", description = "Creates a new order")
    @ApiResponse(responseCode = "200", description = "Order created successfully")
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Order order){
        return ResponseEntity.ok(service.createOrder(order));
    }

    @Operation(summary = "Update order", description = "Updates the order with the specified ID")
    @ApiResponse(responseCode = "200", description = "Order updated successfully")
    @PutMapping("/updateOrder/{modifiedOrderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable String modifiedOrderId){
        return ResponseEntity.ok(service.updateOrder(modifiedOrderId));
    }

    @Operation(summary = "Delete order", description = "Deletes the order with the specified ID")
    @ApiResponse(responseCode = "200", description = "Order deleted successfully")
    @PutMapping("/deleteOrder/{id}")
    public ResponseEntity<Boolean> deleteOrder(@PathVariable String id){
        return ResponseEntity.ok(service.deleteOrder(id));
    }

    @Operation(summary = "Finalize an order", description = "Marks an order as finalized")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order finalized successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/finalize/{id}")
    public ResponseEntity<String> finalizeOrder(@PathVariable String id){
        return ResponseEntity.ok(service.finalizeOrder(id));
    }

    @Operation(summary = "Rate and comment an order", description = "Rates and comments an order")
    @ApiResponse(responseCode = "200", description = "Order rated and commented successfully")
    @PutMapping("/rateAndComment/{id}")
    public ResponseEntity<Order> rateAndCommentOrder(@PathVariable String id, @RequestBody OrderFeedbackDTO orderFeedbackDTO){
        return ResponseEntity.ok(service.rateAndCommentOrder(id, orderFeedbackDTO));
    }
    
    @Operation(summary = "Get current orders", description = "Returns a list of all current orders")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping("/currentOrders/{waiterEmail}")
    public ResponseEntity<List<Order>> getCurrentOrders(@PathVariable String waiterEmail){
        return ResponseEntity.ok(service.getCurrentOrders(waiterEmail));
    }

    @Operation(summary = "Get finalized orders", description = "Returns a list of all finalized orders")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping("/finalizedOrders/{waiterEmail}")
    public ResponseEntity<List<Order>> getFinalizedOrders(@PathVariable String waiterEmail){
        return ResponseEntity.ok(service.getFinalizedOrders(waiterEmail));
    }

    @PutMapping("/addDishes/{orderId}")
    public ResponseEntity<Order> addDishes(@PathVariable String orderId, @RequestBody List<OrderDishes> dishes){
        return ResponseEntity.ok(service.addDishes(dishes, orderId));
    }

    @GetMapping("/currentTableOrder/{tableName}")
    public ResponseEntity<Order> getCurrentTableOrder(@PathVariable String tableName){
        return ResponseEntity.ok(service.getCurrentTableOrder(tableName));
    }
}
