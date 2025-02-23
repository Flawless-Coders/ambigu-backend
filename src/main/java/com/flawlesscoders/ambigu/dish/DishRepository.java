package com.flawlesscoders.ambigu.dish;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DishRepository extends MongoRepository<Dish, String> {
    List<Dish> findByStatus(boolean status);
}
