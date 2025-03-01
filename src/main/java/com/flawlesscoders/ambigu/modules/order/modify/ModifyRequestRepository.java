package com.flawlesscoders.ambigu.modules.order.modify;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifyRequestRepository extends MongoRepository<ModifyRequest, String> {
    
}
