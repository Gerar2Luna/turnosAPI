package com.neoris.turnosrotativos.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import com.neoris.turnosrotativos.entities.Jornada;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface JornadaRepository extends CrudRepository<Jornada,Integer>{
    public List<Jornada> findByNroDocumentoAndFecha(Integer nroDocumento, String fecha);

    public List<Jornada> findByNroDocumento(Integer nroDocumento);

    public List<Jornada> findByFecha(String fecha);
    
    // Realiza una consulta devolviendo una lista con todas las jornadas que corresponden a ese nro documento
    // La fecha se parsea y settea en la creacion de jornada, se decidio agregar un atributo de tipo localdate
    // con la notacion JsonIgnore para que no aparezca en los responsebodys pero que nos ayude con la logica de negocio
    // al poder realizar la comparacion Between con LocalDate que con string no se podria
    public List<Jornada> findByNroDocumentoAndFechaLocalDateBetween(Integer nroDocumento, LocalDate inicioSemana, LocalDate finSemana);
}
