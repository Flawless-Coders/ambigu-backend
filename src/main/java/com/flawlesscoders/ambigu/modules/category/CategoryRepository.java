package com.flawlesscoders.ambigu.modules.category;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.flawlesscoders.ambigu.modules.category.dto.CategoryLite;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByStatus(boolean status);

    @Query(value = "{ status: true }", fields = "{ _id: 1, name: 1 }")
    List<CategoryLite> findActiveCategoryNames();
}
