package com.universidad.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.universidad.model.Estudiante;
import com.universidad.model.Inscripcion;
import com.universidad.model.Materia;
import com.universidad.model.Inscripcion.EstadoInscripcion;
import com.universidad.repository.EstudianteRepository;
import com.universidad.repository.InscripcionRepository;
import com.universidad.repository.MateriaRepository;
import com.universidad.validation.GlobalExceptionHandler;
import com.universidad.exceptions.EstudianteNotFoundException;
import com.universidad.exceptions.MateriaNotFoundException;
import com.universidad.exceptions.RecursoNoDisponibleException;
import com.universidad.exceptions.DuplicateInscripcionException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final MateriaRepository materiaRepository;

    @Transactional
    @CacheEvict(value = {"materias", "materia"}, allEntries = true)
    public Inscripcion crearInscripcion(Long estudianteId, Long materiaId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new EstudianteNotFoundException(estudianteId));
        
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new MateriaNotFoundException(materiaId));

        if (inscripcionRepository.existsByEstudianteAndMateria(estudiante, materia)) {
            throw new DuplicateInscripcionException("El estudiante ya está inscrito en esta materia");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .estudiante(estudiante)
                .materia(materia)
                .estado(EstadoInscripcion.PENDIENTE)
                .fechaInscripcion(LocalDate.now())
                .build();

        return inscripcionRepository.save(inscripcion);
    }
    @Transactional
    @CacheEvict(value = {"materias", "materia"}, allEntries = true)
    public Inscripcion cambiarEstadoInscripcion(Long id, EstadoInscripcion estado) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoDisponibleException("Inscripción no encontrada"));
        inscripcion.setEstado(estado);
        return inscripcionRepository.save(inscripcion);
    }
    @Transactional
    @Cacheable(value = "inscripciones")
    public List<Inscripcion> listarPorEstudiante(Long estudianteId) {
        return inscripcionRepository.findByEstudianteId(estudianteId);
    }
    @Transactional
    @CacheEvict(value = {"materias", "materia"}, allEntries = true)
    public Inscripcion actualizarInscripcion(Long id, Long nuevaMateriaId) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoDisponibleException("Inscripción no encontrada"));
        
        Materia nuevaMateria = materiaRepository.findById(nuevaMateriaId)
                .orElseThrow(() -> new MateriaNotFoundException(nuevaMateriaId));
        
        inscripcion.setMateria(nuevaMateria);
        return inscripcionRepository.save(inscripcion);
    }
    @Transactional
    @CacheEvict(value = {"materias", "materia"}, allEntries = true)
    public void eliminarInscripcion(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoDisponibleException("Inscripción no encontrada"));
        
        if (inscripcion.getEstado() != EstadoInscripcion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden eliminar inscripciones pendientes");
        }
        
        inscripcionRepository.delete(inscripcion);
    }
}
