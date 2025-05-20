package com.universidad.exceptions;

public class DuplicateInscripcionException extends RuntimeException {
    public DuplicateInscripcionException(String message) {
        super(message);
    }
}