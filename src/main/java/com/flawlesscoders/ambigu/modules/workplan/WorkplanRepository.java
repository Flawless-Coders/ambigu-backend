package com.flawlesscoders.ambigu.modules.workplan;

import java.util.List;
import java.util.Optional;
import java.util.Date;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface WorkplanRepository extends MongoRepository<Workplan, String> {
    boolean existsByIsPresent(boolean isPresent);
    Optional<Workplan> findByIsPresent(boolean isPresent); 
     
    @Query("{ 'isFavorite': true }")
    List<Workplan> findByFavoriteWorkplans();
    
    @Query("{ 'isExisting': true }")
    List<Workplan> findByExistingTrue();

    List<Workplan> findByDateAfter(Date date);
}
