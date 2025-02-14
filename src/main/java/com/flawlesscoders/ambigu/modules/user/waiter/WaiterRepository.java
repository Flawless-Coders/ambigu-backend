package com.flawlesscoders.ambigu.modules.user.waiter;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WaiterRepository extends MongoRepository<Waiter, String> {
    /**
    * Obtains all active waiters
    * @return List of active waiters
    */
    List<Waiter> findAllByStatusTrue();
}
