package com.flawlesscoders.ambigu.modules.dish;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    @Operation(summary = "Get all dishes", description = "Returns a list of all dishes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public ResponseEntity<?> getAllDishes() {
        try {
            return ResponseEntity.ok(dishService.getAllDishes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving dishes.");
        }
    }

    @Operation(summary = "Get a dish by ID", description = "Returns a specific dish by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish found"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDishById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(dishService.getDishById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dish not found.");
        }
    }

    @Operation(summary = "Save a new dish", description = "Creates a new dish in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public ResponseEntity<?> saveDish(@RequestBody Dish dish) {
        try {
            return ResponseEntity.ok(dishService.saveDish(dish));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving the dish.");
        }
    }

    @Operation(summary = "Update a dish", description = "Updates an existing dish's data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Dish not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDish(@PathVariable String id, @RequestBody Dish dish) {
        try {
            return ResponseEntity.ok(dishService.updateDish(id, dish));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the dish.");
        }
    }

    @Operation(summary = "Disable a dish", description = "Disables a dish instead of deleting it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish disabled successfully"),
            @ApiResponse(responseCode = "400", description = "Dish cannot be disabled"),
            @ApiResponse(responseCode = "404", description = "Dish not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/disable/{id}")
    public ResponseEntity<?> disableDish(@PathVariable String id) {
        try {
            dishService.disableDish(id);
            return ResponseEntity.ok("Dish disabled successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while disabling the dish.");
        }
    }

    @Operation(summary = "Toggle dish status", description = "Changes the status of a dish between active and inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Dish not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/status/{id}")
    public ResponseEntity<?> toggleStatus(@PathVariable String id) {
        try {
            dishService.toggleStatus(id);
            return ResponseEntity.ok("Dish status changed successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while changing the dish status.");
        }
    }

    @Operation(summary = "Update dish image", description = "Updates the image of a dish using Base64 encoding")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish image updated successfully"),
        @ApiResponse(responseCode = "404", description = "Dish not found"),
        @ApiResponse(responseCode = "400", description = "Invalid image data")
    })
    @PatchMapping(value = "/image/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateDishImage(
        @PathVariable String id,
        @RequestPart("image") MultipartFile image
    ) {
        try {
            dishService.updateDishImage(id, image);
            return ResponseEntity.ok("Image updated successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }
    }
}
