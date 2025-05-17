package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.exceptions.CategoryNotFoundException;
import com.dauphine.blogger.exceptions.PostNotFoundException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.repositories.CategoryRepository;
import com.dauphine.blogger.repositories.PostRepository;
import com.dauphine.blogger.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Post> getAllByCategoryId(UUID categoryId) {
        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        return postRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post getById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    @Override
    public Post create(String title, String content, UUID categoryId) {
        // Validate title and content
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        }

        Post post = new Post(UUID.randomUUID(), title, content, LocalDateTime.now(), category);
        return postRepository.save(post);
    }

    @Override
    public Post update(UUID id, String title, String content) {
        // Validate title and content
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        Post post = getById(id); // Will throw PostIdNotFoundException if not found
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    @Override
    public boolean deleteById(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException(id);
        }

        postRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Post> getAllByTitleOrContentContains(String value) {
        return postRepository.findAllByTitleOrContentContains(value);
    }
}