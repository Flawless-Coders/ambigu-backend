package com.flawlesscoders.ambigu.modules.dish;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.flawlesscoders.ambigu.modules.dish.dto.DishLite;

import java.util.Collection;
import java.util.List;

public interface DishRepository extends MongoRepository<Dish, String> {
    List<Dish> findByStatus(boolean status);

    @Query("{ 'category': ?0 }")
    List<Dish> findDishesByCategory(String categoryId);

    @Query("{ 'status': ?0, 'category': ?1 }")
    List<Dish> findDishesByCategoryAndStatus(boolean available, String categoryId);    

    @Query(value = "{ '_id': { $in: ?0 } }", fields = "{ '_id': 1, 'category': 1 }")
    List<DishLite> findCategoryByIdIn(Collection<String> ids);

    @Query(value = "{ _id: { $in: ?0 } }", fields = "{ _id: 1, name: 1 }")
    List<DishLite> findNameByIdIn(Collection<String> ids);
}
