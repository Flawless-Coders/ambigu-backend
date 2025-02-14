package com.flawlesscoders.ambigu.modules.user.waiter;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface WaiterRepository extends MongoRepository<Waiter, String> {
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter' }")
    List<Waiter> findAllWaiters();

    /**
    * Obtains all active waiters
    * @return List of active waiters
    */
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter' }")
    List<Waiter> findAllByStatusTrue();
}
