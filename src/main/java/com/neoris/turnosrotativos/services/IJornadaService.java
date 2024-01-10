package com.neoris.turnosrotativos.services;

import java.util.List;

import com.neoris.turnosrotativos.entities.Jornada;
import com.neoris.turnosrotativos.entities.dtos.JornadaDTO;

public interface IJornadaService {
    
    public List<Jornada> getAllJornadas();
    public List<Jornada> getByNroDocumento(Integer nroDocumento);
    public List<Jornada> getByFecha(String fecha);
    public List<Jornada> getByNroDocumentoAndFecha(Integer nroDocumento, String fecha);
    public Jornada crearJornada(JornadaDTO jornadaDTO);
    public void validarCreacionJornada(JornadaDTO jornadaDTO);
    public boolean validarHorasTrabajadas(JornadaDTO jornadaDTO);
    public boolean isDiaLibre(JornadaDTO jornadaDTO);
    public boolean validarConceptoMismoTipo(JornadaDTO jornadaDTO);
    public boolean validarSumatoriaHorasTrabajadas(JornadaDTO jornadaDTO);
    public List<Jornada> obtenerJornadasDeLaSemana(JornadaDTO jornadaDTO);
    public boolean validarCargaHorariaSemanal(JornadaDTO jornadaDTO);
    public boolean validarMaximoTurnoExtra(JornadaDTO jornadaDTO);
    public boolean validarMaximoTurnoNormal(JornadaDTO jornadaDTO);
    public boolean validarMaximoDiaLibre(JornadaDTO jornadaDTO);

} 