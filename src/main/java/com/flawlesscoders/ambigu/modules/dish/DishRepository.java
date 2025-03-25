package com.flawlesscoders.ambigu.modules.dish;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DishRepository extends MongoRepository<Dish, String> {
    List<Dish> findByStatus(boolean status);

    @Query("{ 'category': ?0 }")
    List<Dish> findDishesByCategory(String categoryId);

    @Query("{ 'status': ?0, 'category': ?1 }")
    List<Dish> findDishesByCategoryAndStatus(boolean available, String categoryId);    
}
