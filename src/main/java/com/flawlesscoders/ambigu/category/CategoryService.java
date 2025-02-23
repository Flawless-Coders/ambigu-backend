package com.flawlesscoders.ambigu.category;

import com.flawlesscoders.ambigu.dish.Dish;
import com.flawlesscoders.ambigu.dish.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;

    /**
     * Retrieves all categories.
     * @return A list of all categories.
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Searches for a category by its ID.
     * @param id Category ID.
     * @return The found category.
     * @throws ResponseStatusException if the category is not found.
     */
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
    }

    /**
     * Saves a new category in the database.
     * @param category The category to save.
     * @return The saved category.
     * @throws ResponseStatusException if the data is invalid.
     */
    public Category saveCategory(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The category name cannot be empty.");
        }
        category.setStatus(true); // The category is always created as active
        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category.
     * @param id The ID of the category.
     * @param updatedCategory The category object with updated data.
     * @return The updated category.
     * @throws ResponseStatusException if the category is not found.
     */
    public Category updateCategory(String id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setImage(updatedCategory.getImage());

        return categoryRepository.save(existingCategory);
    }

    /**
     * Disables a category instead of deleting it.
     * @param id The ID of the category.
     * @throws ResponseStatusException if the category has associated dishes.
     */
    public void disableCategory(String id) {
        Category category = getCategoryById(id);

        // Check if the category is assigned to any dish
        List<Dish> dishes = dishRepository.findAll();
        for (Dish dish : dishes) {
            if (dish.getCategory().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The category cannot be disabled because it is assigned to one or more dishes.");
            }
        }

        category.setStatus(false);
        categoryRepository.save(category);
    }

    /**
     * Toggles the status of a category between active and inactive.
     * @param id The ID of the category.
     */
    public void toggleStatus(String id) {
        Category category = getCategoryById(id);
        category.setStatus(!category.isStatus());
        categoryRepository.save(category);
    }
}