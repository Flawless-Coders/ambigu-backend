package com.flawlesscoders.ambigu.modules.user.waiter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface WaiterRepository extends MongoRepository<Waiter, String> {
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter' }")
    List<Waiter> findAllWaiters();

    /**
    * Obtains all active waiters
    * @return List of active waiters
    */
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter', 'status': true }")
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
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter', 'status': true, 'isLeader': false }")
    List<Waiter> findAllByStatusTrueAndLeaderFalse();

    /**
     * Obtains the leader
     * @return Leader waiter
     */
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter', 'status': true, 'isLeader': true }")
    Optional<Waiter> findLeader();

    /**
     * Obtains the Top 5 waiters by rating
     * @return List of the Top 5 waiters by rating
     * 
     */
    @Aggregation(pipeline = {
        "{ '$match': { '_class': 'com.flawlesscoders.ambigu.modules.user.waiter.Waiter' } }",
        "{ '$sort': { 'avgRating': -1 } }",
        "{ '$limit': 5 }",
        "{ '$project': { 'name': 1, 'lastname_p': 1,'avgRating': 1, '_id': 0 } }"
    })
    List<Waiter> findTop5ByOrderByAvgRatingDesc();
}
