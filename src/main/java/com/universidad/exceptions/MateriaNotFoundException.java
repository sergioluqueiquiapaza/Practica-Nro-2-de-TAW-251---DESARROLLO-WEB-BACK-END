package com.universidad.exceptions;

public class MateriaNotFoundException extends RuntimeException {
    public MateriaNotFoundException(Long id) {
        super("Materia con ID " + id + " no encontrada");
    }
}