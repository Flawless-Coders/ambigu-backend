package com.flawlesscoders.ambigu.modules.menu;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends MongoRepository<Menu, String> {
    List<Menu> findByStatus(boolean status);

    @Query("{ 'status': true }")
    Optional<Menu> getCurrentMenu();
    
    @Query(value = "{}", sort = "{ 'status': -1 }")
    List<Menu> findAllOrderByStatusDesc();
}
