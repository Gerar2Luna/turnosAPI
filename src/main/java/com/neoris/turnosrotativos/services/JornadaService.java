package com.neoris.turnosrotativos.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neoris.turnosrotativos.entities.Jornada;
import com.neoris.turnosrotativos.entities.dtos.JornadaDTO;
import com.neoris.turnosrotativos.exceptions.BadRequestException;
import com.neoris.turnosrotativos.repositories.JornadaRepository;

@Service
public class JornadaService {
    @Autowired
    JornadaRepository jornadaRepository;
    
    @Autowired
    EmpleadoService empleadoService;

    @Autowired
    ConceptoService conceptoService;

    public List<Jornada> getAllJornadas(){
        return (List<Jornada>)jornadaRepository.findAll();
    }

    public List<Jornada> getByNroDocumento(Integer nroDocumento){
        return jornadaRepository.findByNroDocumento(nroDocumento);
    }

    public List<Jornada> getByFecha(String fecha){
        return jornadaRepository.findByFecha(fecha);
    }

    public List<Jornada> getByNroDocumentoAndFecha(Integer nroDocumento, String fecha){
        return jornadaRepository.findByNroDocumentoAndFecha(nroDocumento, fecha);
    }


    public Jornada crearJornada(JornadaDTO jornadaDTO){
        this.validarCreacionJornada(jornadaDTO);
        Jornada jornada = new Jornada();
        jornada.setConcepto((conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre())); // Utiliza el dato del dto para buscar el concepto en el repo
        jornada.setNroDocumento((empleadoService.obtenerInfoEmpleado(jornadaDTO.getIdEmpleado()).get().getNroDocumento()));
        jornada.setNombreCompleto(empleadoService.getNombreCompleto(empleadoService.obtenerInfoEmpleado(jornadaDTO.getIdEmpleado()).get()));
        jornada.setFecha(jornadaDTO.getFecha());
        jornada.setHsTrabajadas(jornadaDTO.getHorasTrabajadas());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        jornada.setFechaLocalDate(LocalDate.parse(jornadaDTO.getFecha(), formatter));
        return jornadaRepository.save(jornada);
    }

    // Se llama en cadena la validacion de jornadas
    public void validarCreacionJornada(JornadaDTO jornadaDTO){
        this.validarHorasTrabajadas(jornadaDTO);
        this.isDiaLibre(jornadaDTO);
        this.validarConceptoMismoTipo(jornadaDTO);
        this.validarSumatoriaHorasTrabajadas(jornadaDTO);
        System.out.println("Fecha, "+jornadaDTO.getFecha());
        this.validarCargaHorariaSemanal(jornadaDTO);
        this.validarMaximoTurnoExtra(jornadaDTO);
        this.validarMaximoTurnoNormal(jornadaDTO);
        this.validarMaximoDiaLibre(jornadaDTO);
    }


    public boolean validarHorasTrabajadas(JornadaDTO jornadaDTO){
        // Verifica si el concepto corresponde a dia libre y retorna bad request si ingreso hsTrabajadas

        if(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Dia Libre") 
                                                && jornadaDTO.getHorasTrabajadas()!=null){
            throw new BadRequestException("El concepto ingresado no requiere el ingreso de hsTrabajadas");
        } 
        // Verifica si el concepto corresponde a turno normal o extra
        else if (conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Turno Normal")
                    || conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Turno Extra"))
                {
                    // Si es un concepto normal o extra valida si se ingresaron las horas trabajadas
                    if(jornadaDTO.getHorasTrabajadas()==null){
                        throw new BadRequestException("hsTrabajadas es obligatorio para el concepto ingresado");
                    }
                    // Verifica que las horas trabajadas esten dentro del rango
                    if (jornadaDTO.getHorasTrabajadas()>conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getHsMaximo()
                        || jornadaDTO.getHorasTrabajadas()<conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getHsMinimo()
                        ){
                            throw new BadRequestException("El rango de horas que se puede cargar para este concepto es de "+
                            conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getHsMinimo()+" - "+
                            conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getHsMaximo());
                        }
                } 
        return true;
    }
    // Recorre la lista de jornadas por nro documento y fecha, obtiene las jornadas de ese empleado, luego verifica en todas las jornadas
    // posibles para ese dia si alguna ya es dia libre y de ser asi termina la ejecucion devolviendo el badrequest asociado

    public boolean isDiaLibre(JornadaDTO jornadaDTO){
        List<Jornada> jornadasDelEmpleado = this.getByNroDocumentoAndFecha(empleadoService.
                                                obtenerInfoEmpleado(jornadaDTO.getIdEmpleado()).get().getNroDocumento(),
                                                jornadaDTO.getFecha());
        boolean contieneDiaLibre = jornadasDelEmpleado.stream().anyMatch(jornada -> jornada.getConcepto().equals("Dia Libre"));
        if(contieneDiaLibre){
            throw new BadRequestException("El empleado ingresado cuenta con un día libre en esa fecha");
        }
        return true;
    }



    public boolean validarConceptoMismoTipo(JornadaDTO jornadaDTO){
        List<Jornada> jornadasDelEmpleado = this.getByNroDocumentoAndFecha(empleadoService.
                                                obtenerInfoEmpleado(jornadaDTO.getIdEmpleado()).get().getNroDocumento(),
                                                jornadaDTO.getFecha());
        // Este boolean recorre todas las jornadasDelEmpleado y devuelve true si alguno de los conceptos coincide con el String "Turno Normal"
        boolean contieneTurnoNormal = jornadasDelEmpleado.stream().anyMatch(jornada -> jornada.getConcepto().equals("Turno Normal"));

        // Este boolean recorre todas las jornadasDelEmpleado y devuelve true si alguno de los conceptos coincide con el String "Turno Extra"
        boolean contieneTurnoExtra = jornadasDelEmpleado.stream().anyMatch(jornada -> jornada.getConcepto().equals("Turno Extra"));

        // Consulta si ya hay un turno normal cargado y si lo que se carga por dto tambien es un turno normal, entonces dispara la excepcion asociada
        if(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Turno Normal") && contieneTurnoNormal==true){
            throw new BadRequestException("El empleado ya tiene registrado una jornada con este concepto en la fecha ingresada");
        }
        // Consulta si ya hay un turno extra cargado y si lo que se carga por dto tambien es un turno extra, entonces dispara la excepcion asociada
        if(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Turno Extra") && contieneTurnoExtra==true){
            throw new BadRequestException("El empleado ya tiene registrado una jornada con este concepto en la fecha ingresada");
        }
        
        // Consulta si ya hay un turno cargado y si lo que se carga por dto es un dia libre, entonces dispara la excepcion asociada
        if(jornadasDelEmpleado.size()>=1 && conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Dia Libre")){
            throw new BadRequestException("El empleado no puede cargar Dia Libre si cargo un turno previamente para la fecha ingresada");
        }

        // Si pasa todas las validaciones se devuelve true
        return true;
    }

    public boolean validarSumatoriaHorasTrabajadas(JornadaDTO jornadaDTO){
        List<Jornada> jornadasDelEmpleado = this.getByNroDocumentoAndFecha(empleadoService.
                                                obtenerInfoEmpleado(jornadaDTO.getIdEmpleado()).get().getNroDocumento(),
                                                jornadaDTO.getFecha());
        // Al tener la lista de jornadas del empleado, solo es necesario acumularlas y verificar que la suma de las hs ingresadas por el dto
        // No sean mayores a 12
        Integer totalHorasTrabajadas = 0;
        for (Jornada jornada : jornadasDelEmpleado){
            if(!(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Dia Libre"))){
            totalHorasTrabajadas += jornada.getHsTrabajadas();
            }
        } // En ambos casos se verifica si no es un dia libre para evitar que .getHorasTrabajadas devuelva null
        // Esto no afecta la logica del programa ya que un dia libre no hay que acumularlo
        // Si se intenta ingresar un dia libre cuando ya hay una jornada cargada para la fecha, hay otro metodo que lo valida
            if (!(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Dia Libre"))){
            if(totalHorasTrabajadas+jornadaDTO.getHorasTrabajadas()>12){
                throw new BadRequestException("El empleado no puede cargar más de 12 horas trabajadas en un día.");
            }
        } 
        return true;
    }

    // Este metodo busca segun la fecha del dto el lunes y sabado correspondiente a esa fecha (semana habil)
    public List<Jornada> obtenerJornadasDeLaSemana(JornadaDTO jornadaDTO){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fecha = LocalDate.parse(jornadaDTO.getFecha(), formatter);
        // Usando previous or same nos aseguramos de que si es lunes se incluya, si es otro dia busca el lunes
        LocalDate inicioSemana = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // Se consigue el lunes
        LocalDate finSemana = inicioSemana.plusDays(6); // Se consigue el sabado

        return jornadaRepository.findByNroDocumentoAndFechaLocalDateBetween(
                                empleadoService.obtenerInfoEmpleado(jornadaDTO.getIdEmpleado()).get().getNroDocumento()
                                ,inicioSemana, finSemana);
    }

    public boolean validarCargaHorariaSemanal(JornadaDTO jornadaDTO){
        List<Jornada> jornadasSemanales = this.obtenerJornadasDeLaSemana(jornadaDTO);
        Integer cargaHorariaSemanal=0;
        // Se verifica si es dia libre para evitar que get.HsTrabajadas de null
        for (Jornada jornada : jornadasSemanales){
            if(!(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Dia Libre"))){
                cargaHorariaSemanal+=jornada.getHsTrabajadas();
            } 
        }
        // Esta verificacion se realiza antes de cargarla en el repositorio, por lo que si la suma supera el maximo no se realiza la carga
        // Y se devuelve el bad request
        if(cargaHorariaSemanal > 48){
            throw new BadRequestException("El empleado ingresado supera las 48 horas semanales.");
        }
        return true;
    }

    public boolean validarMaximoTurnoExtra(JornadaDTO jornadaDTO){
        // Obtenemos las jornadas de la semana asociada a la fecha del dto
        List<Jornada> jornadasSemanales = this.obtenerJornadasDeLaSemana(jornadaDTO);
        Integer cantidadTurnosExtra=0;
        // Recorremos la lista de jornadas preguntando si alguna corresponde a turno extra entonces la contamos
        for(Jornada jornada: jornadasSemanales){
            if(jornada.getConcepto().equals("Turno Extra"));
            cantidadTurnosExtra++;
        }
        // Preguntamos si el concepto del dto es turno extra y si al sumarlo a la lista semanal, esto superaria el maximo, entonces disparamos la exception
        if(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Turno Extra") && (cantidadTurnosExtra+1)>3){
            throw new BadRequestException("El empleado ingresado ya cuenta con 3 turnos extra esta semana.");
        }
        return true;
    }

    public boolean validarMaximoTurnoNormal(JornadaDTO jornadaDTO){
        // Obtenemos las jornadas de la semana asociada a la fecha del dto
        List<Jornada> jornadasSemanales = this.obtenerJornadasDeLaSemana(jornadaDTO);
        Integer cantidadTurnosNormales=0;
        // Recorremos la lista de jornadas preguntando si alguna corresponde a turno extra entonces la contamos
        for(Jornada jornada : jornadasSemanales){
            if(jornada.getConcepto().equals("Turno Normal")){
                cantidadTurnosNormales++;
            }
        }
        // Preguntamos si el concepto del dto es turno normal y si al sumarlo a la lista semanal, esto superaria el maximo, entonces disparamos la exception
        if(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Turno Normal") && (cantidadTurnosNormales+1)>5){
            throw new BadRequestException("El empleado ingresado ya cuenta con 5 turnos normales esta semana");
        }
        return true;
    }

    public boolean validarMaximoDiaLibre(JornadaDTO jornadaDTO){
        // Obtenemos las jorndas de la semana asociada ala fecha del dto
        List<Jornada> jornadasSemanales = this.obtenerJornadasDeLaSemana(jornadaDTO);
        Integer cantidadDiaLibre=0;
        // Recorremos la lista de jornadas preguntando si alguna corresponde a Dia Libre entonces la contamos
        for(Jornada jornada : jornadasSemanales){
            if(jornada.getConcepto().equals("Dia Libre")){
                cantidadDiaLibre++;
            }
        }
        // Preguntamos si el concepto del dto es Dia Libre y si al sumarlo a la lista semanal, esto superaria el maximo, entonces disparamos la exception
        if(conceptoService.buscarConceptoPorId(jornadaDTO.getIdConcepto()).getNombre().equals("Dia Libre") && (cantidadDiaLibre+1)>2){
            throw new BadRequestException("El empleado no cuenta con más días libres esta semana");
        }
        return true;
    }

}
