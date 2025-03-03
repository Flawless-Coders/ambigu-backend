package com.flawlesscoders.ambigu.modules.menu;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends MongoRepository<Menu, String> {
    @Query("{ 'status': true }")
    Menu getCurrentMenu();
}
