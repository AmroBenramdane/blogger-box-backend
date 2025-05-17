package com.dauphine.blogger.services.impl;


import com.dauphine.blogger.exceptions.CategoryNameAlreadyExistsException;
import com.dauphine.blogger.exceptions.CategoryNameNotFoundException;
import com.dauphine.blogger.exceptions.CategoryNotFoundException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.repositories.CategoryRepository;
import com.dauphine.blogger.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.categoryRepository = repository;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public Category create(String name) {
        if (existsByName(name)) {
            throw new CategoryNameAlreadyExistsException(name);
        }

        Category category = new Category(UUID.randomUUID(), name);
        return categoryRepository.save(category);
    }

    @Override
    public Category update(UUID id, String name) {
        Category category = getById(id); // Will throw CategoryIdNotFoundException if not found

        // Check if another category with the same name already exists
        if (!category.getName().equalsIgnoreCase(name) && existsByName(name)) {
            throw new CategoryNameAlreadyExistsException(name);
        }

        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    public boolean deleteById(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }

        categoryRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Category> getAllLikeName(String name) {
        List<Category> categories = categoryRepository.findAllLikeName(name);
        if (categories.isEmpty()) {
            throw new CategoryNameNotFoundException(name);
        }
        return categories;
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}