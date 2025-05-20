package com.universidad.exceptions;

public class DocenteNotFoundException extends RuntimeException {
    public DocenteNotFoundException(Long id) {
        super("Docente con ID " + id + " no encontrado");
    }
}