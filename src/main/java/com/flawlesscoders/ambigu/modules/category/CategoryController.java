package com.flawlesscoders.ambigu.modules.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Returns a list of all categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            return ResponseEntity.ok(categoryService.getAllCategories());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving categories.");
        }
    }

    @Operation(summary = "Get a category by ID", description = "Returns a specific category by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
        }
    }

    @Operation(summary = "Save a new category", description = "Creates a new category in the database.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "500", description = "Server error")
})
@PostMapping(consumes = {"multipart/form-data"})
public ResponseEntity<?> saveCategory(
        @RequestParam("name") String name,
        @RequestPart(value = "image", required = false) MultipartFile image
) {
    try {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(name, image));
    } catch (ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while saving the category.");
    }
}

    @Operation(summary = "Update a category", description = "Updates an existing category's data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable String id, @RequestBody Category category) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, category));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the category.");
        }
    }

    @Operation(summary = "Disable a category", description = "Disables a category instead of deleting it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category disabled successfully"),
            @ApiResponse(responseCode = "400", description = "Category is assigned to one or more dishes"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/disable/{id}")
    public ResponseEntity<?> disableCategory(@PathVariable String id) {
        try {
            categoryService.disableCategory(id);
            return ResponseEntity.ok("Category disabled successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while disabling the category.");
        }
    }

    @Operation(summary = "Toggle category status", description = "Changes the status of a category between active and inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/status/{id}")
    public ResponseEntity<?> toggleStatus(@PathVariable String id) {
        try {
            categoryService.toggleStatus(id);
            return ResponseEntity.ok("Category status toggled successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while changing the category status.");
        }
    }

    @Operation(summary = "Update category image", description = "Updates the image of a category using Base64 encoding")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category image updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Invalid image data")
    })
    @PatchMapping(value = "/image/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateCategoryImage(
        @PathVariable String id,
        @RequestPart("image") MultipartFile image
    ) {
        try {
            categoryService.updateCategoryImage(id, image);
            return ResponseEntity.ok("Image updated successfully.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }
    }

    @GetMapping("/getByStatus/{status}")
    public ResponseEntity<List<Category>> getByStatus(@PathVariable boolean status){
        return ResponseEntity.ok(categoryService.findByStatus(status));
    }
}