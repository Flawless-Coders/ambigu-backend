package com.flawlesscoders.ambigu.menu;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuRepository extends MongoRepository<Menu, String>{
    List<Menu> findByStatus(boolean status);
    
}
