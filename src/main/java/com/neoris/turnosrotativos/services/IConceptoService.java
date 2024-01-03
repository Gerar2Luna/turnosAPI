package com.neoris.turnosrotativos.services;

import java.util.List;

import com.neoris.turnosrotativos.entities.Concepto;

/**
 * IConceptoService
 */
public interface IConceptoService {

    public List<Concepto> obtenerConceptos();
    public Concepto buscarConceptoPorId(Integer id);
    
}