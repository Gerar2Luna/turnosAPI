package com.neoris.turnosrotativos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.neoris.turnosrotativos.entities.Concepto;
import com.neoris.turnosrotativos.services.ConceptoService;

@RestController
@RequestMapping("/concepto")
public class ConceptoController {
    @Autowired
    ConceptoService conceptoService;

    @GetMapping
    public ResponseEntity<List<Concepto>> obtenerConceptos(){
        return new ResponseEntity<List<Concepto>>(conceptoService.obtenerConceptos(),HttpStatus.OK);
    }

}
