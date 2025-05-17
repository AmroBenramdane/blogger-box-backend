package com.dauphine.blogger.controllers;

import com.dauphine.blogger.dto.CreationCategoryRequest;
import com.dauphine.blogger.dto.UpdateCategoryRequest;
import com.dauphine.blogger.exceptions.CategoryNameAlreadyExistsException;
import com.dauphine.blogger.exceptions.CategoryNotFoundException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/categories")
@Tag(name = "Category API", description = "Operations for managing categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieve all categories or filter like name"
    )
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) String name) {
        List<Category> categories = name == null || name.isBlank()
                ? service.getAll()
                : service.getAllLikeName(name);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a single category identified by its ID")
    public ResponseEntity<Category> retrieveCategoryById(@PathVariable("id") UUID id) throws CategoryNotFoundException {
        Category category = service.getById(id);
        if (category == null) {
            throw new CategoryNotFoundException(id);
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new category with the provided name")
    public ResponseEntity<Category> createCategory(@RequestBody CreationCategoryRequest request)
            throws CategoryNameAlreadyExistsException {
        if (service.existsByName(request.getName())) {
            throw new CategoryNameAlreadyExistsException(request.getName());
        }

        Category category = service.create(request.getName());
        return ResponseEntity
                .created(URI.create("/v1/categories/" + category.getId()))
                .body(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Updates the name of an existing category")
    public ResponseEntity<Category> updateCategory(
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request)
            throws CategoryNotFoundException, CategoryNameAlreadyExistsException {

        // Check if category exists
        Category existingCategory = service.getById(id);
        if (existingCategory == null) {
            throw new CategoryNotFoundException(id);
        }

        // Check if name already exists for a different category
        if (!existingCategory.getName().equalsIgnoreCase(request.getName())
                && service.existsByName(request.getName())) {
            throw new CategoryNameAlreadyExistsException(request.getName());
        }

        Category updatedCategory = service.update(id, request.getName());
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category by its ID")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) throws CategoryNotFoundException {
        boolean deleted = service.deleteById(id);
        if (!deleted) {
            throw new CategoryNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }
}