package com.flawlesscoders.ambigu.dish;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DishRepository extends MongoRepository<Dish, String>{
    List<Dish> findByStatus(boolean status);
}
