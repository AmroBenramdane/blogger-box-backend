package com.dauphine.blogger.services;


import com.dauphine.blogger.models.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> getAll();
    Category getById(UUID id);
    Category create(String name);
    Category update(UUID id, String name);
    boolean deleteById(UUID id);
    boolean existsByName(String name);
    List<Category> getAllLikeName(String name);
}