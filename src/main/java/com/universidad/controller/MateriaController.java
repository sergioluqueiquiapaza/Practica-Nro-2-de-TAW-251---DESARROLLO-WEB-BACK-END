package com.universidad.controller;

import com.universidad.model.Materia;
import com.universidad.service.IMateriaService;

import jakarta.transaction.Transactional;

import com.universidad.dto.MateriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
@Tag(name = "Materias", description = "CRUD de Materias y Asignación de Docentes")
@SecurityRequirement(name = "bearer-key")
public class MateriaController {

    private final IMateriaService materiaService;
    private static final Logger logger = LoggerFactory.getLogger(MateriaController.class);

    @Autowired
    public MateriaController(IMateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Obtener todas las materias", description = "Acceso público")
    @GetMapping
    public ResponseEntity<List<MateriaDTO>> obtenerTodasLasMaterias() {
        long inicio = System.currentTimeMillis();
        logger.info("[MATERIA] Inicio obtenerTodasLasMaterias: {}", inicio);
        List<MateriaDTO> result = materiaService.obtenerTodasLasMaterias();
        long fin = System.currentTimeMillis();
        logger.info("[MATERIA] Fin obtenerTodasLasMaterias: {} (Duracion: {} ms)", fin, (fin-inicio));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaDTO> obtenerMateriaPorId(@PathVariable Long id) {
        long inicio = System.currentTimeMillis();
        logger.info("[MATERIA] Inicio obtenerMateriaPorId: {}", inicio);
        MateriaDTO materia = materiaService.obtenerMateriaPorId(id);
        long fin = System.currentTimeMillis();
        logger.info("[MATERIA] Fin obtenerMateriaPorId: {} (Duracion: {} ms)", fin, (fin-inicio));
        if (materia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(materia);
    }

    @GetMapping("/codigo/{codigoUnico}")
    public ResponseEntity<MateriaDTO> obtenerMateriaPorCodigoUnico(@PathVariable String codigoUnico) {
        MateriaDTO materia = materiaService.obtenerMateriaPorCodigoUnico(codigoUnico);
        if (materia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(materia);
    }

    @PostMapping
    @Operation(summary = "Crear nueva materia", description = "Requiere rol ADMIN")
    public ResponseEntity<MateriaDTO> crearMateria(
        @Valid @RequestBody MateriaDTO materia, @RequestParam @NotNull(message = "El ID del docente es requerido") Long docenteId) {
        //MateriaDTO materiaDTO = new MateriaDTO(materia.getId(), materia.getNombre(), materia.getCodigoUnico());
        MateriaDTO nueva = materiaService.crearMateria(materia);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }



    @GetMapping("/formaria-circulo/{materiaId}/{prerequisitoId}") // Endpoint para verificar si una materia formaría un círculo con un prerequisito
    @Transactional // Anotación que indica que este método debe ejecutarse dentro de una transacción
    public ResponseEntity<Boolean> formariaCirculo(@PathVariable Long materiaId, @PathVariable Long prerequisitoId) {
        MateriaDTO materiaDTO = materiaService.obtenerMateriaPorId(materiaId); // Obtiene la materia por su ID
        if (materiaDTO == null) { // Verifica si la materia existe
            return ResponseEntity.notFound().build();
        }
        Materia materia = new Materia(materiaDTO.getId(), materiaDTO.getNombreMateria(), materiaDTO.getCodigoUnico());
        // Crea una nueva instancia de Materia con los datos obtenidos
        // Verifica si agregar el prerequisito formaría un círculo
        boolean circulo = materia.formariaCirculo(prerequisitoId); // Llama al método formariaCirculo de la clase Materia
        if (circulo) { // Si formaría un círculo, retorna un error 400 Bad Request
            return ResponseEntity.badRequest().body(circulo);
        }
        return ResponseEntity.ok(circulo);
    }
    @PostMapping("/{id}/asignar-docente")
    @Transactional
    @Operation(summary = "Asignar docente a materia", description = "Requiere rol ADMIN")
    public ResponseEntity<MateriaDTO> asignarDocenteAMateria(
            @PathVariable Long id,
            @RequestParam Long docenteId) {
        MateriaDTO materiaActualizada = materiaService.asignarDocente(id, docenteId);
        return ResponseEntity.ok(materiaActualizada);
    }
    @Operation(summary = "Actualizar materia por ID", description = "Requiere rol ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<MateriaDTO> actualizarMateria(
        @PathVariable Long id, 
        @Valid @RequestBody MateriaDTO materiaDTO
    ) {
        MateriaDTO actualizada = materiaService.actualizarMateria(id, materiaDTO);
        return ResponseEntity.ok(actualizada);
    }

    @Operation(summary = "Eliminar materia por ID", description = "Requiere rol ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMateria(@PathVariable Long id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }
}
