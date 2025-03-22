package com.flawlesscoders.ambigu.modules.user.waiter;

import java.util.List;
import java.util.Optional;

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

    /**
     * Obtain waiter by email
     * @param email Email of the waiter
     * @return Waiter object
     */
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter', 'email': ?0 }")
    Optional<Waiter> findByEmail(String email);

    /**
     * Obtains all active waiters who aren't leaders
     * @return List of active waiters who aren't leaders
     */
    @Query("{ 'status': true, 'isLeader': false }")
    List<Waiter> findAllByStatusTrueAndLeaderFalse();

    /**
     * Obtains the leader
     * @return Leader waiter
     */
    @Query("{ 'status': true, 'isLeader': true }")
    Optional<Waiter> findLeader();


}
