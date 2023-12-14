package com.neoris.turnosrotativos.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@Entity(name = "jornadas")
public class Jornada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true,nullable = false)
    private Integer id;

    
    private Integer nroDocumento;
    
    private String nombreCompleto;

    
    private String fecha;

    
    private String concepto;

    // De esta manera logramos quitarlo del JSON si las horas son null cumpliendo con el criterio opcional
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer hsTrabajadas;

    @JsonIgnore
    private LocalDate fechaLocalDate;
}
