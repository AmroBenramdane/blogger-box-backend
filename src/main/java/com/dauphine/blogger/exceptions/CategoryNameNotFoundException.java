package com.dauphine.blogger.exceptions;

public class CategoryNameNotFoundException extends RuntimeException {

    public CategoryNameNotFoundException(String name) {
        super(String.format("Category not found with name: '%s'", name));
    }
}