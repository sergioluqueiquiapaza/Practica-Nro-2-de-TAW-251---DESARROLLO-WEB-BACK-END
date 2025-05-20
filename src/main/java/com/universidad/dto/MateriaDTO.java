package com.universidad.dto;

import java.io.Serializable;
import java.util.List;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaDTO implements Serializable {

    @NotNull(message = "El ID no puede ser nulo")
    private Long id;

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombreMateria;

    @NotBlank(message = "El código único es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}-\\d{3}$", message = "Formato de código inválido (ej: ABC-123)")
    private String codigoUnico;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Mínimo 1 crédito")
    @Max(value = 12, message = "Máximo 12 créditos")
    private Integer creditos;

    // Nuevo campo para docente asignado
    private Long docenteId;

    
    // Campos existentes...
    private List<Long> prerequisitos;


    private List<Long> esPrerequisitoDe;
}