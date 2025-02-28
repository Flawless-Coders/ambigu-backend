package com.flawlesscoders.ambigu.modules.menu;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.flawlesscoders.ambigu.modules.category.Category;
import com.flawlesscoders.ambigu.modules.dish.Dish;
import com.flawlesscoders.ambigu.utils.config.FileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.core.io.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;

/**
 * Controlador para gestionar los menús.
*/
@RestController
@AllArgsConstructor
@RequestMapping("api/menu")
public class MenuController {

    private final MenuService menuService;
    private final FileService fileService;

    @Operation(summary = "Get a menu by ID", description = "Returns a menu based on its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Menu.class)) }),
        @ApiResponse(responseCode = "404", description = "Menu not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Menu> findById(@PathVariable String id){
        Menu menu = menuService.findById(id);
        return ResponseEntity.ok(menu);
    }


    @Operation(summary = "Get menu photo", description = "Returns the image associated with a specific menu")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photo retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Photo not found")
    })
    @GetMapping("/photo/{photoId}")
    public ResponseEntity<Resource> getFile(@PathVariable String photoId) {
        Optional<GridFsResource> fileOptional = fileService.getFile(photoId);

        if (fileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        GridFsResource gridFsResource = fileOptional.get();

        String contentType = gridFsResource.getContentType();
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + gridFsResource.getFilename() + "\"")
                .body((Resource) gridFsResource); 
    }

    @Operation(summary = "Get dishes by menu, category and the status of the menu", description = "Returns a list of dishes belonging to a specific menu and category based on their menu status (enabled/disabled)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish list retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Menu or category not found")
    })
        @GetMapping("/getDishes/{menuId}/{categoryId}/{status}")
        public ResponseEntity<List<Dish>> getDishesByMenu(@PathVariable String menuId, @PathVariable String categoryId, @PathVariable boolean status){
        return ResponseEntity.ok(menuService.getDishesByMenu(menuId, categoryId, status));
        }
    
        @Operation(summary = "Get categories by menu", description = "Returns a list of categories belonging to a specific menu")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category list retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Menu not found")
        })
        @GetMapping("/category/{menuId}")
        public ResponseEntity<List<Category>> getCategoriesByMenu(@PathVariable String menuId){
            return ResponseEntity.ok(menuService.getCategoriesByMenu(menuId));
        }


        @Operation(summary = "Save a new menu", description = "Creates a new menu in the database")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "500", description = "Server error")
        })

        @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        public ResponseEntity<?> save(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("photo") MultipartFile photo
        ) {
            try {
                if (photo.isEmpty()) {
                    return ResponseEntity.badRequest().body("El archivo no puede estar vacío.");
                }

                Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/jpg");
                String contentType = photo.getContentType();

                if (contentType == null || !allowedTypes.contains(contentType)) {
                    return ResponseEntity.badRequest().body("Formato de archivo no soportado. Usa JPEG o PNG.");
                }
                
                // Guardar la imagen en GridFS y obtener su ID
                String photoId = fileService.saveFile(photo);
                Menu menu = Menu.builder().name(name).description(description).photoId(photoId).build();

                // Guardar el menú en la base de datos
                menuService.save(menu);

                return ResponseEntity.ok(menu);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }


        @Operation(summary = "Update a menu", description = "Updates an existing menu's data")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "500", description = "Server error")
        })
        @PutMapping(value="/{id}",consumes = "multipart/form-data")
        public ResponseEntity<?>update(
            @PathVariable String id,
            @RequestParam(value="name") String name,
            @RequestParam(value="description") String description,
            @RequestParam(value = "photo", required = false) MultipartFile photo 
        ){
            try {
                String photoId = null;
            if (photo != null && !photo.isEmpty()) {
                photoId = fileService.saveFile(photo);
            }
            return ResponseEntity.ok(menuService.update(id, name, description, photoId));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        
        }

        @Operation(summary = "Change menu status", description = "Toggles the status of a menu between active and inactive")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Menu not found")
        })
        @PutMapping("/status/{id}")
        public ResponseEntity<Void> changeStatus(@PathVariable String id) {
            menuService.changeStatus(id);
            return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Add a dish to a menu", description = "Associates a dish with an existing menu")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish added successfully"),
            @ApiResponse(responseCode = "404", description = "Menu or dish not found")
        })
        @PutMapping("/addDish/{dishId}/{menuId}")
        public ResponseEntity<?> addDish (@PathVariable String dishId, @PathVariable String menuId){
            menuService.addDish(dishId, menuId);
            return ResponseEntity.ok().build();
        }

        @Operation(summary = "Remove a dish from a menu", description = "Dissociates a dish from a menu")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish removed successfully"),
            @ApiResponse(responseCode = "404", description = "Menu or dish not found")
        })
        @PutMapping("/removeDish/{dishId}/{menuId}")
        public ResponseEntity<?> removeDish (@PathVariable String dishId, @PathVariable String menuId){
            menuService.removeDish(dishId, menuId);
            return ResponseEntity.ok().build();
        }

        
        @Operation(summary = "Get all menus", description = "Returns a list of menus based on their status")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Menu.class)) }),
            @ApiResponse(responseCode = "500", description = "Server error")
        })
        @GetMapping("/findAll/{status}")
        public ResponseEntity<List<Menu>> findAll(@PathVariable boolean status){
            List<Menu> menus = menuService.getByStatus(status);
            return ResponseEntity.ok(menus);
        }

    }
