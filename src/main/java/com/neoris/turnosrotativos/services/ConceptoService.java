package com.neoris.turnosrotativos.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neoris.turnosrotativos.entities.Concepto;
import com.neoris.turnosrotativos.exceptions.BadRequestException;
import com.neoris.turnosrotativos.repositories.ConceptoRepository;

@Service
public class ConceptoService implements IConceptoService {
    @Autowired
    ConceptoRepository conceptoRepository;

    @Override
    public List<Concepto> obtenerConceptos(){
        List<Concepto> listaCompleta = (List<Concepto>)conceptoRepository.findAll();
        List<Concepto> listaFiltrada = listaCompleta.stream().
                        filter(e->e.getHsMinimo()!=null && e.getHsMaximo()!=null). // Aca se filtra en la lista de conceptos aquellos que no tienen hs
                        collect(Collectors.toList());                   // minima y maxima, por logica actual serian del tipo Dia Libre
        return listaFiltrada;
    }

    @Override
    public Concepto buscarConceptoPorId(Integer id){
        if(conceptoRepository.existsById(id)){
            return conceptoRepository.findById(id).get();
        }
        else throw new BadRequestException("No existe el concepto ingresado");
    }

}
