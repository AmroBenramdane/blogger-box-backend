package com.dauphine.blogger.exceptions;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String name) {
        super(String.format("Category with name '%s' already exists", name));
    }
}