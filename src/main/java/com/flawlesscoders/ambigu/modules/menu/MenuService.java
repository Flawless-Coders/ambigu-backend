package com.flawlesscoders.ambigu.modules.menu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.modules.category.Category;
import com.flawlesscoders.ambigu.modules.category.CategoryRepository;
import com.flawlesscoders.ambigu.modules.dish.Dish;
import com.flawlesscoders.ambigu.modules.dish.DishRepository;

import lombok.AllArgsConstructor;

/**
 * Servicio para la gestión de menús en el sistema.
 */
@Service
@AllArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Searches for a meny by its ID.
     * @param id Menu ID.
     * @return the found menu.
     * @throws ResponseStatusException if the menu is not found.
     */
    public Menu findById(String id){
        return menuRepository.findById(id).orElseThrow(()-> 
        new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));
    }


    /**
     * Gets the categories of a menu.
     * @param menuId The ID of the menu.
     * @return A list of categories for the menu.
     */
     public List<Category> getCategoriesByMenu(String menuId){
        Menu menu = menuRepository.findById(menuId)
        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if(menu.getCategories()==null){
            menu.setCategories(new ArrayList<>());
        }

        return categoryRepository.findAllById(menu.getCategories());
     }

    /**
     * Gets the dishes of a menu by category.
     * @param menuId The ID of the menu.
     * @param categoryId The ID of the category.
     * @param status The status of the menu.
     * @return A list of dishes in the specified category.
     */
     public List<Dish> getDishesByMenu(String menuId, String categoryId, boolean status){
        Menu menu = menuRepository.findById(menuId).
        orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if (menu.getDishes() == null || menu.getDishes().isEmpty()) {
            return new ArrayList<>();
        }

        List<Dish> dishes = dishRepository.findAllById(menu.getDishes());
        List<Dish> activeDish = new ArrayList<>();

        for(Dish dish : dishes){
            if(dish.isStatus() && dish.getCategory().equals(categoryId) && menu.isStatus()==status){
                activeDish.add(dish);
            }
        }
        return activeDish;
     }

    /**
     * Saves a new menu to the database.
     * @param menu The menu to save.
     * @return The saved menu.
     * @throws ResponseStatusException if the data is invalid.
     */
    public Menu save(Menu menu){
        if (menu.getName() == null || menu.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
        }
        if (menu.getDescription() == null || menu.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción no puede estar vacía");
        }
        menu.setStatus(true);
        return menuRepository.save(menu);
       
    }


    /**
     * Updates an existing menu.
     * @param id The ID of the menu.
     * @param name The new name of the menu.
     * @param description The new description of the menu.
     * @param photoId The ID of the new photo (optional).
     * @return The updated menu.
     * @throws ResponseStatusException if the menu is not found or the data is invalid.
     */
    public Menu update(String id, String name, String description, String photoId){

        Menu existingMenu = menuRepository
        .findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
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
     * Changes the status of a menu between active and inactive.
     * @param id The ID of the menu.
     */
    public void changeStatus(String id){
        Menu menu = menuRepository.findById(id).
        orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));
           menu.setStatus(!menu.isStatus());
           menuRepository.save(menu);
        
     }

    /**
     * Adds a dish to a menu.
     * @param dishId The ID of the dish.
     * @param menuId The ID of the menu.
     * @return true if the dish was added, false if it was already present.
     * @throws ResponseStatusException if the dish or the menu do not exist.
     */
     public boolean addDish(String dishId, String menuId){
        Dish dish = dishRepository.findById(dishId)
        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el platillo"));
       
        Menu menu = menuRepository.findById(menuId).
        orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));

        if (menu.getDishes() == null) {
        menu.setDishes(new ArrayList<>()); 
        }

        if (menu.getCategories() == null) {
            menu.setCategories(new ArrayList<>()); 
        }


        if(!menu.getDishes().contains(dishId)){
            menu.getDishes().add(dishId);

            if(!menu.getCategories().contains(dish.getCategory())){
                menu.getCategories().add(dish.getCategory());
            }

            menuRepository.save(menu);
            return true;
        }
        return false;
     }

    /**
     * Removes a dish from a menu.
     * @param dishId The ID of the dish.
     * @param menuId The ID of the menu.
     * @return true if the dish was removed.
     * @throws ResponseStatusException if the dish or the menu do not exist.
     */

    public boolean removeDish(String dishId, String menuId) {
    if (!dishRepository.existsById(dishId)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el platillo");
    }
    Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el menú"));
    if (menu.getDishes() == null || menu.getDishes().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El menú no tiene platillos para eliminar");
    }
    boolean removed = menu.getDishes().remove(dishId);

    if (!removed) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El platillo no estaba en el menú");
    }

    menuRepository.save(menu);
    return true;
 }
        
}
