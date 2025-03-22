package com.flawlesscoders.ambigu.modules.dish;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DishRepository extends MongoRepository<Dish, String> {
    List<Dish> findByStatus(boolean status);

    @Query("{ 'category': ?0 }")
    List<Dish> findDishesByCategory(String categoryId);
}
