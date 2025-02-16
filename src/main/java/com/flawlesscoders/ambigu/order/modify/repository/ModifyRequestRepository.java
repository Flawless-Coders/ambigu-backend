package com.flawlesscoders.ambigu.order.modify.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.flawlesscoders.ambigu.order.modify.model.ModifyRequest;

@Repository
public interface ModifyRequestRepository extends MongoRepository<ModifyRequest, String> {
    
}
