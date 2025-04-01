package com.flawlesscoders.ambigu.modules.dish;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import java.io.IOException;

import com.flawlesscoders.ambigu.modules.category.Category;
import com.flawlesscoders.ambigu.modules.category.CategoryRepository;
import com.flawlesscoders.ambigu.modules.menu.Menu;
import com.flawlesscoders.ambigu.modules.menu.MenuRepository;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Retrieves all dishes.
     * 
     * @return A list of all dishes.
     */
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    /**
     * Searches for a dish by its ID.
     * 
     * @param id Dish ID.
     * @return The found dish.
     * @throws ResponseStatusException if the dish is not found.
     */
    public Dish getDishById(String id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found."));
    }

    /**
     * Saves a new dish in the database.
     * 
     * @param dish The dish to save.
     * @return The saved dish.
     * @throws ResponseStatusException if the data is invalid.
     */
    public Dish saveDish(Dish dish) {
        if (dish.getName() == null || dish.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The dish name cannot be empty.");
        }
        if (dish.getDescription() == null || dish.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The dish description cannot be empty.");
        }
        if (dish.getName().length()>25) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del platillo no puede ser tan larg0.");
        }
        if (dish.getDescription().length()>120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción del platillo no puede ser tan larga.");
        }

        dish.setStatus(true); // The dish is always created as active
        return dishRepository.save(dish);
    }

    /**
     * Updates an existing dish.
     * 
     * @param id          The ID of the dish.
     * @param updatedDish The dish object with updated data.
     * @return The updated dish.
     * @throws ResponseStatusException if the dish is not found.
     */
    public Dish updateDish(String id, Dish updatedDish) {
        Dish existingDish = getDishById(id);
        if (updatedDish.getName() == null || updatedDish.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The dish name cannot be empty.");
        }
        if (updatedDish.getDescription() == null || updatedDish.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The dish description cannot be empty.");
        }
        if (updatedDish.getName().length()>25) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del platillo no puede ser tan largo.");
        }
        if (updatedDish.getDescription().length()>120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción del platillo no puede ser tan larga.");
        }

        existingDish.setName(updatedDish.getName());
        existingDish.setDescription(updatedDish.getDescription());
        existingDish.setCategory(updatedDish.getCategory());
        existingDish.setPrice(updatedDish.getPrice());
        existingDish.setImageBase64(updatedDish.getImageBase64());
        return dishRepository.save(existingDish);
    }

    /**
     * Disables a dish instead of deleting it.
     * 
     * @param id The ID of the dish.
     * @throws ResponseStatusException if the dish is linked to an active menu.
     */
    public void disableDish(String id) {
        Dish dish = getDishById(id);

        // Check if the dish is in any menu
        List<Menu> menus = menuRepository.findAll();
        for (Menu menu : menus) {
            if (menu.getDishes().contains(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The dish cannot be disabled because it is assigned to a menu.");
            }
        }

        dish.setStatus(false);
        dishRepository.save(dish);
    }

    /**
     * Toggles the status of a dish between active and inactive.
     * 
     * @param id The ID of the dish.
     */
    public void toggleStatus(String id) {
        Dish dish = getDishById(id);
        dish.setStatus(!dish.isStatus());
        dishRepository.save(dish);
    }

    public void updateDishImage(String id, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen no puede estar vacía.");
        }

        try {
            Dish dish = getDishById(id);
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            dish.setImageBase64(base64Image);
            dishRepository.save(dish);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al convertir la imagen a Base64.");
        }
    }

    /**
     * Find all available or unavailable dishes by their category and status
     * 
     * @param available The status of the category.
     * @param categoryId The ID of the category.
     */
    public List<Dish> getDishesByCategoryAndStatus(boolean available, String categoryId) {
        try {
            if(available){
                return dishRepository.findDishesByCategoryAndStatus(true, categoryId);
            }else{
                return dishRepository.findDishesByCategoryAndStatus(false, categoryId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error al obtener los platillos: " + e.getMessage());
        }
    }

}
