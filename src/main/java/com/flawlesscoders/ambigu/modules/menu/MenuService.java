package com.flawlesscoders.ambigu.modules.menu;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.flawlesscoders.ambigu.modules.category.Category;
import com.flawlesscoders.ambigu.modules.category.CategoryRepository;
import com.flawlesscoders.ambigu.modules.dish.Dish;
import com.flawlesscoders.ambigu.modules.dish.DishRepository;
import lombok.RequiredArgsConstructor;
import com.flawlesscoders.ambigu.modules.menu.dto.MenuDTO;

/**
 * Service for managing menus in the system.
 */
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;

    @Value("${frontend.url}")
    private String url;

    /**
     * Searches for a menu by its ID.
     * 
     * @param id Menu ID.
     * @return The found menu.
     * @throws ResponseStatusException If the menu is not found.
     */
    public Menu findById(String id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));
    }

    /**
     * Gets the categories of the current menu.
     * 
     * @return A list of categories for the current menu.
     */
    public List<Category> getCategoriesByMenu() {
        Menu menu = menuRepository.getCurrentMenu()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if (menu.getCategories() == null) {
            menu.setCategories(new ArrayList<>());
        }

        List<Category> allCategories = categoryRepository.findAllById(menu.getCategories());
        List<Category> activeCategories = new ArrayList<>();

        for (Category c : allCategories) {
            if (c.isStatus()) {
                activeCategories.add(c);
            }
        }

        return activeCategories;
    }

    /**
     * Gets the categories of a disabled menu.
     * 
     * @param menuId The ID of the menu.
     * @return A list of categories for the disabled menu.
     * @throws ResponseStatusException If the menu is not found.
     */
    public List<Category> getCategoriesByMenu(String menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));
        if (menu.getCategories() == null) {
            menu.setCategories(new ArrayList<>());
        }

        List<Category> allCategories = categoryRepository.findAllById(menu.getCategories());
        List<Category> activeCategories = new ArrayList<>();

        for (Category c : allCategories) {
            if (c.isStatus()) {
                activeCategories.add(c);
            }
        }

        return activeCategories;
    }

    /**
     * Gets the dishes of a disabled menu by category.
     * 
     * @param menuId     The ID of the menu.
     * @param categoryId The ID of the category.
     * @return A list of dishes in the specified category.
     * @throws ResponseStatusException If the menu is not found.
     */
    public List<Dish> getDishesByMenu(String menuId, String categoryId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

        if (menu.getDishes() == null || menu.getDishes().isEmpty()) {
            return new ArrayList<>();
        }

        List<Dish> dishes = dishRepository.findAllById(menu.getDishes());
        List<Dish> activeDish = new ArrayList<>();

        for (Dish dish : dishes) {
            if (dish.isStatus() && dish.getCategory().equals(categoryId)) {
                activeDish.add(dish);
            }
        }
        return activeDish;
    }

    /**
     * Gets the dishes by category of the current menu.
     * 
     * @param categoryId The ID of the category.
     * @return A list of dishes in the specified category.
     */
    public List<Dish> getDishesByCategory(String categoryId) {
        Menu menu = menuRepository.getCurrentMenu()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if (menu.getDishes() == null || menu.getDishes().isEmpty()) {
            return new ArrayList<>();
        }

        List<Dish> dishes = dishRepository.findAllById(menu.getDishes());
        List<Dish> activeDish = new ArrayList<>();

        for (Dish dish : dishes) {
            if (dish.isStatus() && dish.getCategory().equals(categoryId)) {
                activeDish.add(dish);
            }
        }
        return activeDish;
    }

    /**
     * Saves a new menu to the database.
     * 
     * @param menu The menu to save.
     * @return The saved menu.
     * @throws ResponseStatusException If the data is invalid.
     */
    public Menu save(Menu menu) {
        if (menu.getName() == null || menu.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
        }
        if (menu.getDescription() == null || menu.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción no puede estar vacía");
        }
        menu.setStatus(false);
        return menuRepository.save(menu);
    }

    /**
     * Updates an existing menu.
     * 
     * @param id          The ID of the menu.
     * @param name        The new name of the menu.
     * @param description The new description of the menu.
     * @param photoId     The ID of the new photo (optional).
     * @return The updated menu.
     * @throws ResponseStatusException If the menu is not found or the data is
     *                                 invalid.
     */
    public Menu update(String id, String name, String description, String photoId) {
        Menu existingMenu = menuRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del menú no puede estar vacío");
        }
        if (description == null || description.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción no puede estar vacía");
        }

        if (photoId != null) {
            existingMenu.setPhotoId(photoId);
        }

        existingMenu.setName(name);
        existingMenu.setDescription(description);

        return menuRepository.save(existingMenu);
    }

    /**
     * Assigns as a current menu
     * 
     * @param id The ID of the menu.
     * @throws ResponseStatusException If the menu is not found.
     */
    public boolean assingAsCurrent(String id) {
        Menu currentMenu = menuRepository.getCurrentMenu()
                .orElse(null);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if (currentMenu != null) {
            currentMenu.setStatus(false);
            menuRepository.save(currentMenu);
        }
        menu.setStatus(true);
        menuRepository.save(menu);
        return true;

    }

    /**
     * Adds a dish to a menu.
     * 
     * @param dishId The ID of the dish.
     * @param menuId The ID of the menu.
     * @return true if the dish was added, false if it was already present.
     * @throws ResponseStatusException If the dish or the menu does not exist.
     */
    public boolean addDish(String dishId, String menuId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Platillo no encontrado"));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu no encontrado"));

        if (menu.getDishes() == null) {
            menu.setDishes(new ArrayList<>());
        }

        if (menu.getCategories() == null) {
            menu.setCategories(new ArrayList<>());
        }

        if (!menu.getDishes().contains(dishId)) {
            menu.getDishes().add(dishId);

            if (!menu.getCategories().contains(dish.getCategory())) {
                menu.getCategories().add(dish.getCategory());
            }

            menuRepository.save(menu);
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El platillo ya está en el menú");
        }
    }

    /**
     * Removes a dish from a menu. If the dish was the only one in its category, the
     * category is also removed.
     * 
     * @param dishId The ID of the dish.
     * @param menuId The ID of the menu.
     * @return true if the dish was removed.
     * @throws ResponseStatusException If the dish or the menu does not exist.
     */
    public boolean removeDish(String dishId, String menuId) {
        Dish dishRemoved = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

        if (menu.getDishes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The menu has no dishes to remove");
        }

        boolean removed = menu.getDishes().remove(dishId);
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The dish was not in the menu");
        }

        List<Dish> remainingDishes = dishRepository.findAllById(menu.getDishes());

        // Check if there are other dishes in the same category
        boolean categoryHasOtherDishes = remainingDishes.stream()
                .anyMatch(dish -> dish.getCategory().equals(dishRemoved.getCategory()));

        // If no dishes remain in the category, remove the category from the menu
        if (!categoryHasOtherDishes) {
            menu.getCategories().remove(dishRemoved.getCategory());
        }

        menuRepository.save(menu);
        return true;
    }

    /**
     * Gets the menus based on their status (active/inactive).
     * 
     * @param status The status of the menu.
     * @return A list of menus with the specified status.
     */
    public List<Menu> findAll() {
        return menuRepository.findAllOrderByStatusDesc();
    }

    /**
     * Assigns as a current menu
     * 
     * @param id The ID of the menu.
     * @throws ResponseStatusException If the menu is not found.
     */
    public boolean inactivateMenu(String id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu no encontrado"));
        menu.setStatus(false);
        menuRepository.save(menu);
        return true;
    }

    public boolean isCurrentMenu() {
        Menu currentMenu = menuRepository.getCurrentMenu().orElse(null);
        return currentMenu != null;
    }

    public Menu getCurrentMenu() {
        Menu currentMenu = menuRepository.getCurrentMenu().orElse(null);
        return currentMenu;
    }

    public List<MenuDTO> getCategoriesAndDishes() {
        List<MenuDTO> dishesByCategory = new ArrayList<>();
        Menu currentMenu = menuRepository.getCurrentMenu().orElse(null);

        for (String categoryId : currentMenu.getCategories()) {
            Category category = new Category();
            MenuDTO categoryAndDishes = new MenuDTO();
            List<Dish> dishesList = new ArrayList<>();

            category = categoryRepository.findById(categoryId).orElse(null);
            if (category.isStatus()) {
                categoryAndDishes.setCategory(category);

                for (String dishId : currentMenu.getDishes()) {
                    Dish dish = new Dish();
                    dish = dishRepository.findById(dishId).orElse(null);

                    if (dish.getCategory().equals(category.getId()) && dish.isStatus() && category.isStatus()) {
                        dishesList.add(dish);
                    }

                }
                if (dishesList.size() > 0) {
                    categoryAndDishes.setDishes(dishesList);
                    dishesByCategory.add(categoryAndDishes);
                }

            }
        }

        return dishesByCategory;
    }

    public String getMenuURL() {
        return url + "/public-menu";
    }
}