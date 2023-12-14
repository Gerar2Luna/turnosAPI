package com.neoris.turnosrotativos.entities.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JornadaDTO {
    @NotNull
    private Integer idEmpleado;
    @NotNull
    private Integer idConcepto;
    @NotBlank
    private String fecha;
    
    private Integer horasTrabajadas;
}
