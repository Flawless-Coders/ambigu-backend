package com.flawlesscoders.ambigu.modules.order.modify;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifyRequestRepository extends MongoRepository<ModifyRequest, String> {
    List<ModifyRequest> findByDeletedRequest(boolean deletedRequest);
}
