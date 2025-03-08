package com.flawlesscoders.ambigu.modules.workplan.historyDisabled;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DisabledTableHistoryRepository extends MongoRepository<DisabledTableHistory, String>{
     @Query("{ 'workplanId': ?0 }") 
    List<DisabledTableHistory> findByWorkplanId(String workplanId);
}
