package com.flawlesscoders.ambigu.modules.table;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TableRepository extends MongoRepository<Table, String>{
    List<Table> findByIsEnabledTrue();

    List<Table> findByIsEnabledFalse(); 

     @Query("{ 'isEnabled': true, 'tableWaiter': false }")
    List<Table> findEnabledTablesWithoutWaiter();

    @Query("{ 'isEnabled': true, 'tableWaiter': true }")
    List<Table> findEnabledTablesWithWaiter();

    @Query("{ 'isEnabled': false, 'tableWaiter': false }")
    List<Table> findDisabledTablesWithoutWaiter();

}