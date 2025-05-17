package com.dauphine.blogger.controllers;

import com.dauphine.blogger.dto.CreationPostRequest;
import com.dauphine.blogger.dto.UpdatePostRequest;
import com.dauphine.blogger.exceptions.CategoryNotFoundException;
import com.dauphine.blogger.exceptions.PostNotFoundException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.services.CategoryService;
import com.dauphine.blogger.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/posts")
@Tag(name = "Post API", description = "Operations for managing blog posts")
public class PostController {

    private final PostService service;
    private final CategoryService categoryService;

    public PostController(PostService service, CategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all posts", description = "Returns all posts ordered by creation date (newest first)")
    public ResponseEntity<List<Post>> retrieveAllPosts(@RequestParam(required = false) String value) {
        List<Post> posts = value == null || value.isBlank()
                ? service.getAll()
                : service.getAllByTitleOrContentContains(value);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID", description = "Returns a single post identified by its ID")
    public ResponseEntity<Post> retrievePostById(@PathVariable UUID id) throws PostNotFoundException {
        Post post = service.getById(id);
        if (post == null) {
            throw new PostNotFoundException(id);
        }
        return ResponseEntity.ok(post);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get posts by category", description = "Returns all posts belonging to a specific category")
    public ResponseEntity<List<Post>> retrievePostsByCategoryId(@PathVariable UUID categoryId)
            throws CategoryNotFoundException {
        // Check if category exists
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException(categoryId);
        }

        List<Post> posts = service.getAllByCategoryId(categoryId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    @Operation(summary = "Create a new post", description = "Creates a new blog post")
    public ResponseEntity<Post> createPost(@RequestBody CreationPostRequest request)
            throws CategoryNotFoundException {
        // Validate that the category exists if provided
        if (request.getCategoryId() != null) {
            Category category = categoryService.getById(request.getCategoryId());
            if (category == null) {
                throw new CategoryNotFoundException(request.getCategoryId());
            }
        }

        Post post = service.create(request.getTitle(), request.getContent(), request.getCategoryId());
        return ResponseEntity
                .created(URI.create("/v1/posts/" + post.getId()))
                .body(post);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a post", description = "Updates an existing blog post")
    public ResponseEntity<Post> updatePost(
            @PathVariable UUID id,
            @RequestBody UpdatePostRequest request) throws PostNotFoundException {

        Post post = service.update(id, request.getTitle(), request.getContent());
        if (post == null) {
            throw new PostNotFoundException(id);
        }
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post", description = "Deletes a blog post by its ID")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) throws PostNotFoundException {
        boolean deleted = service.deleteById(id);
        if (!deleted) {
            throw new PostNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }
}