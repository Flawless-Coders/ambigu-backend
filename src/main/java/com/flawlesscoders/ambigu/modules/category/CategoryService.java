package com.flawlesscoders.ambigu.modules.category;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import com.flawlesscoders.ambigu.modules.dish.Dish;
import com.flawlesscoders.ambigu.modules.dish.DishRepository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;

    private void validateCategoryName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoría no puede estar vacío.");
        }

        name = name.trim();

        List<Category> existingCategories = categoryRepository.findAll();
        for (Category existing : existingCategories) {
            if (existing.getName().trim().equalsIgnoreCase(name.trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Ya existe una categoría con ese nombre.");
            }
        }
    }

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
    public Category saveCategory(String name, MultipartFile image) {
        validateCategoryName(name);
    
        // Crear una nueva categoría
        Category category = new Category();
        category.setName(name.trim());
        category.setStatus(true);
    
        if (image != null && !image.isEmpty()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                category.setImageBase64(base64Image);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al procesar la imagen.");
            }
        }
    
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
    
        if (updatedCategory.getName() != null && !updatedCategory.getName().isBlank()) {
            validateCategoryName(updatedCategory.getName());
            existingCategory.setName(updatedCategory.getName().trim());
        }
    
        existingCategory.setStatus(updatedCategory.isStatus());
    
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

    public void updateCategoryImage(String id, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen no puede estar vacía.");
        }

        try {
            Category category = getCategoryById(id);
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            category.setImageBase64(base64Image);
            categoryRepository.save(category);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al convertir la imagen a Base64.");
        }
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

    public List<Category> findByStatus(boolean status){
        return categoryRepository.findByStatus(status);
    }
    
}