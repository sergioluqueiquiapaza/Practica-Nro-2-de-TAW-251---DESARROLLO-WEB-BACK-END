package com.universidad.service.impl;

import com.universidad.model.Docente;
import com.universidad.model.Materia;
import com.universidad.repository.DocenteRepository;
import com.universidad.repository.MateriaRepository;
import com.universidad.service.IMateriaService;
import com.universidad.validation.GlobalExceptionHandler;

import jakarta.transaction.Transactional;

import com.universidad.dto.MateriaDTO;
import com.universidad.exceptions.DocenteNotFoundException;
import com.universidad.exceptions.MateriaNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaServiceImpl implements IMateriaService {

    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private DocenteRepository docenteRepository;

    // Método utilitario para mapear Materia a MateriaDTO
    private MateriaDTO mapToDTO(Materia materia) {
        if (materia == null) return null;
        return MateriaDTO.builder()
                .id(materia.getId())
                .nombreMateria(materia.getNombreMateria())
                .codigoUnico(materia.getCodigoUnico())
                .creditos(materia.getCreditos())
                .prerequisitos(materia.getPrerequisitos() != null ?
                    materia.getPrerequisitos().stream().map(Materia::getId).collect(Collectors.toList()) : null)
                .esPrerequisitoDe(materia.getEsPrerequisitoDe() != null ?
                    materia.getEsPrerequisitoDe().stream().map(Materia::getId).collect(Collectors.toList()) : null)
                .build();
    }

    @Override
    @Transactional
    @Cacheable(value = "materias")
    public List<MateriaDTO> obtenerTodasLasMaterias() {
        return materiaRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Cacheable(value = "materia", key = "#id")
    public MateriaDTO obtenerMateriaPorId(Long id) {
        return materiaRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    @Override
    @Transactional
    @Cacheable(value = "materia", key = "#codigoUnico")
    public MateriaDTO obtenerMateriaPorCodigoUnico(String codigoUnico) {
        Materia materia = materiaRepository.findByCodigoUnico(codigoUnico);
        return mapToDTO(materia);
    }

    @Override
    @Transactional
    @CachePut(value = "materia", key = "#result.id")
    @CacheEvict(value = "materias", allEntries = true)
    public MateriaDTO crearMateria(MateriaDTO materiaDTO) {
        Materia materia = new Materia();
        materia.setNombreMateria(materiaDTO.getNombreMateria());
        materia.setCodigoUnico(materiaDTO.getCodigoUnico());
        materia.setCreditos(materiaDTO.getCreditos());
        // Map other fields as necessary
        Materia savedMateria = materiaRepository.save(materia);
        return mapToDTO(savedMateria);
    }

    @Override
    @Transactional
    @CachePut(value = "materia", key = "#id")
    @CacheEvict(value = "materias", allEntries = true)
    public MateriaDTO actualizarMateria(Long id, MateriaDTO materiaDTO) {
        Materia materia = materiaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Materia not found"));
        materia.setNombreMateria(materiaDTO.getNombreMateria());
        materia.setCodigoUnico(materiaDTO.getCodigoUnico());
        materia.setCreditos(materiaDTO.getCreditos());
        // Map other fields as necessary
        Materia updatedMateria = materiaRepository.save(materia);
        return mapToDTO(updatedMateria);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"materia", "materias"}, allEntries = true)
    public void eliminarMateria(Long id) {
        materiaRepository.deleteById(id);
    }
    @Override
    @Transactional
    @CacheEvict(value = {"materias", "materia"}, allEntries = true)
    public MateriaDTO asignarDocente(Long materiaId, Long docenteId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new MateriaNotFoundException(materiaId));
        
        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new DocenteNotFoundException(docenteId));

        materia.setDocente(docente);
        Materia materiaActualizada = materiaRepository.save(materia);
        return mapToDTO(materiaActualizada);
    }
    @Override
    @Transactional
    @CacheEvict(value = {"materias", "materia"}, allEntries = true)
    public MateriaDTO agregarPrerequisito(Long materiaId, Long prerequisitoId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new MateriaNotFoundException(materiaId));
        Materia prerequisito = materiaRepository.findById(prerequisitoId)
                .orElseThrow(() -> new MateriaNotFoundException(prerequisitoId));
        
        materia.getPrerequisitos().add(prerequisito);
        return mapToDTO(materiaRepository.save(materia));
    }
}
