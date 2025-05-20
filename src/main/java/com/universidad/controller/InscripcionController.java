package com.universidad.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.model.Inscripcion;
import com.universidad.model.Inscripcion.EstadoInscripcion;
import com.universidad.service.impl.InscripcionServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones", description = "Gestión de inscripciones a materias")
@SecurityRequirement(name = "bearer-key")
public class InscripcionController {

    @Autowired
    private InscripcionServiceImpl inscripcionService;

    @Operation(summary = "Crear inscripción", description = "Requiere rol ESTUDIANTE o ADMIN")
    @PostMapping
    public ResponseEntity<Inscripcion> crearInscripcion(
        @RequestParam Long estudianteId,
        @RequestParam Long materiaId
    ) {
        Inscripcion nueva = inscripcionService.crearInscripcion(estudianteId, materiaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @Operation(summary = "Listar inscripciones por estudiante")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Inscripcion>> listarPorEstudiante(@PathVariable Long estudianteId) {
        List<Inscripcion> inscripciones = inscripcionService.listarPorEstudiante(estudianteId);
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(summary = "Aprobar/Rechazar inscripción", description = "Requiere rol DOCENTE o ADMIN")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Inscripcion> cambiarEstado(
        @PathVariable Long id,
        @RequestParam EstadoInscripcion estado
    ) {
        Inscripcion actualizada = inscripcionService.cambiarEstadoInscripcion(id, estado);
        return ResponseEntity.ok(actualizada);
    }
    @PreAuthorize("hasRole('ESTUDIANTE') or hasRole('ADMIN')")
    @Operation(summary = "Actualizar inscripción", description = "Requiere rol ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<Inscripcion> actualizarInscripcion(
        @PathVariable Long id,
        @RequestParam Long nuevaMateriaId
    ) {
        Inscripcion actualizada = inscripcionService.actualizarInscripcion(id, nuevaMateriaId);
        return ResponseEntity.ok(actualizada);
    }
    @PreAuthorize("hasRole('ESTUDIANTE') or hasRole('ADMIN')")
    @Operation(summary = "Eliminar inscripción", description = "Solo para inscripciones en estado PENDIENTE y Requiere rol ESTUDIANTE o ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInscripcion(@PathVariable Long id) {
        inscripcionService.eliminarInscripcion(id);
        return ResponseEntity.noContent().build();
    }
}

