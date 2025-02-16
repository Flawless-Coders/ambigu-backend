package com.flawlesscoders.ambigu.order.modify.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlesscoders.ambigu.order.modify.model.ModifyRequest;
import com.flawlesscoders.ambigu.order.modify.service.ModifyRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/modify")
@AllArgsConstructor
public class ModifyRequestController {
    private final ModifyRequestService service;

    @Operation(summary = "Get all modification requests", description = "Returns a list of all modification requests")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ModifyRequest>> getAllRequests(){
        return ResponseEntity.ok(service.getAllRequests());
    }

    @Operation(summary = "Request order modification", description = "Submits a request to modify an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Modification request submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/upadateOrderRequest")
    public ResponseEntity<ModifyRequest> modifyRequest(@RequestBody ModifyRequest modifyRequest){
        return ResponseEntity.ok(service.sendModifyRequest(modifyRequest));
    }

    @Operation(summary = "Request order deletion", description = "Submits a request to delete an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deletion request submitted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/deleteOrderRequest/{id}")
    public ResponseEntity<ModifyRequest> deleteOrderRequest(@PathVariable String id){
        return ResponseEntity.ok(service.sendDeleteRequest(id));
    }
}
