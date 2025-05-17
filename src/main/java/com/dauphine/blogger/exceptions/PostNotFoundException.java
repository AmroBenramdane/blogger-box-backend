package com.dauphine.blogger.exceptions;

import java.util.UUID;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(UUID id) {
        super(String.format("Post not found with id: '%s'", id));
    }
}