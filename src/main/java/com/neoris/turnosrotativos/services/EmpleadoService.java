package com.neoris.turnosrotativos.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import com.neoris.turnosrotativos.entities.Empleado;
import com.neoris.turnosrotativos.exceptions.BadRequestException;
import com.neoris.turnosrotativos.exceptions.ConflictoException;
import com.neoris.turnosrotativos.exceptions.RecursoNoEncontradoException;
import com.neoris.turnosrotativos.repositories.EmpleadoRepository;

@Service
public class EmpleadoService {
    
    @Autowired
    EmpleadoRepository empleadoRepository;

    public List<Empleado> obtenerEmpleados() { // GET ALL Empleados
        
        return (List<Empleado>)empleadoRepository.findAll();

    }

    public ResponseEntity<Object> crearEmpleado(@Valid Empleado empleado){
        this.validarCreacionEmpleado(empleado);
        LocalDateTime fechaCreacion = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        empleado.setFechaCreacion(fechaCreacion.format(formatter));
        return new ResponseEntity<>(empleadoRepository.save(empleado), HttpStatus.CREATED);
    }

    public Optional<Empleado> obtenerInfoEmpleado(Integer id){
        if(empleadoExists(id)){
        return empleadoRepository.findById(id);
        }
        else throw new BadRequestException("No se encontró al empleado con Id: "+id);

    }

    // Devuelve un response entity Empleado con los valores actualizados y codigo 200 de realizarse correctamente
    // Caso contrario retorna el mensaje asociado a la excepcion correspondiente (ver CustomExceptionHandler)

    public ResponseEntity<Empleado> actualizarEmpleado(@Valid Empleado empleadoActualizado, @PathVariable Integer id) {
        if (empleadoExists(id)) {
            this.validarActualizarEmpleado(empleadoActualizado,id);
            int iden = id;
            empleadoActualizado.setId(iden); 
            empleadoActualizado.setFechaCreacion(this.empleadoRepository.findById(iden).get().getFechaCreacion());
            this.empleadoRepository.save(empleadoActualizado);
            return new ResponseEntity<Empleado>(obtenerInfoEmpleado(id).get(), HttpStatus.OK);
        } else {
            throw new RecursoNoEncontradoException("No se encontró el empleado con Id:"+id);
            
        }
    }
    
    // Devuelve un ResponseEntity Object con codigo 204 en caso de que se elimine correctamente
    // Caso contrario retorna el mensaje asociado a la excepcion correspondiente (ver CustomExceptionHandler)

    public ResponseEntity<Object> eliminarEmpleadoPorId(@PathVariable Integer id){
        if (empleadoExists(id)){ // Verifica existencia de empleado
            int iden = id;
            this.empleadoRepository.deleteById(iden);
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        } else {
            throw new RecursoNoEncontradoException("No se encontró el empleado con Id:"+id);
        }
    }

    public boolean empleadoExists(Integer empleadoID){
        Optional<Empleado> empleadOptional = (empleadoRepository.findById(empleadoID));
        return empleadOptional.isPresent();
    }

    // Metodos de validacion de negocio

    // Invoca en cadena a todos los metodos de validacion para el post empleado, solo para mejorar legibilidad 
    public void validarCreacionEmpleado(Empleado empleado){
        this.validarDocumento(empleado.getNroDocumento());
        this.validarFechaNacimiento(empleado.getFechaNacimiento());
        this.validarEdad(empleado.getFechaNacimiento());
        this.validarFormatoEmail(empleado.getEmail());
        this.validarEmailDuplicado(empleado.getEmail());
        this.validarFechaIngreso(empleado.getFechaIngreso());
        this.validarFormatoNombre(empleado.getNombre(),empleado.getApellido());
    }

    public void validarActualizarEmpleado(Empleado empleado, Integer id){
        this.validarFechaNacimiento(empleado.getFechaNacimiento());
        this.validarEdad(empleado.getFechaNacimiento());
        this.validarActualizacionDocumento(empleado.getNroDocumento(),id);
        this.validarActualizacionEmail(empleado.getEmail(),id);
        this.validarFormatoEmail(empleado.getEmail());
        this.validarFechaIngreso(empleado.getFechaIngreso());
        this.validarFormatoNombre(empleado.getNombre(), empleado.getApellido());
    }
    // Valida si es mayor de edad
    public boolean validarEdad(String fechaNacimiento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formatter);
        LocalDate fechaActual = LocalDate.now();
        int edad = fechaActual.minusYears(fechaNac.getYear()).getYear(); // Resta a los años de la fecha actual los años de la fecha de nacimiento y devuelve ese valor con
                                                                        //  el get year del final
        // Verificar si es mayor de 18 o si está cumpliendo 18 años este año
        if( edad > 18 || (edad == 18 && fechaNac.getMonthValue() <= fechaActual.getMonthValue() && fechaNac.getDayOfMonth() <= fechaActual.getDayOfMonth())){
            return true;
        }
        else throw new BadRequestException("La edad del empleado no puede ser menor a 18 años");
    }

    // Valida que la fehca de nacimiento no sea posterior al dia de la fecha
    public boolean validarFechaNacimiento(String fechaNacimiento){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formatter);
        LocalDate fechaActual = LocalDate.now();
        if (fechaNac.isAfter(fechaActual)){
            throw new BadRequestException("La fecha de nacimiento no puede ser posterior al día de la fecha.");
        }
        else return true;
    }
    // Valida que la fecha ingreso no sea posterior al dia de la fecha
    public boolean validarFechaIngreso(String fechaIngreso){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaIngre = LocalDate.parse(fechaIngreso, formatter);
        LocalDate fechaActual = LocalDate.now();
        if (fechaIngre.isAfter(fechaActual)){
            throw new BadRequestException("La fecha de ingreso no puede ser posterior al día de la fecha.");
        }
        else return true;
    }

    // Valida que en el nombre no hayan caracteres restringidos
    public boolean validarFormatoNombre(String nombre, String apellido){
        String patron = "[^a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(nombre);
        Matcher matcher2 = pattern.matcher(apellido);
        if(matcher.find()){
            throw new BadRequestException("Solo se permiten letras en el campo nombre");
        }
        if(matcher2.find()){
            throw new BadRequestException("Solo se permiten letras en el campo apellido");
        }
        return true;
    }
    // Valida si no existe un empleado con ese documento ya creado
    public boolean validarDocumento(Integer nroDocumento){
        Optional<Empleado> empleadoABuscar = empleadoRepository.findByNroDocumento(nroDocumento);
        if(empleadoABuscar.isPresent()){
            throw new ConflictoException("Ya existe un empleado con el documento ingresado.");
        }
        else return true;
    }

    // Valida que el campo email tenga formato valido
    public boolean validarFormatoEmail(String email){
        String patron = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        else throw new BadRequestException("El email ingresado no es correcto.");
    }

    // Valida si se intenta registrar un empleado con un email ya registrado
    public boolean validarEmailDuplicado(String email){
        if(empleadoRepository.findByEmail(email).isPresent()){
            throw new ConflictoException("Ya existe un empleado con el email ingresado.");
        }
        else return true;
    }

    public boolean validarActualizacionEmail(String email, Integer id){
        if(empleadoRepository.findByEmail(email).isPresent()){
            if(id==empleadoRepository.findByEmail(email).get().getId()){
                return true;
            }
            else throw new ConflictoException("Ya existe un empleado con el email ingresado.");
        }
        else
            return true;
    }

    public boolean validarActualizacionDocumento(Integer nroDocumento, Integer id){
        if(empleadoRepository.findByNroDocumento(nroDocumento).isPresent()){
            if(id==(empleadoRepository.findByNroDocumento(nroDocumento).get().getId())){
                return true;
            }
            else throw new ConflictoException("Ya existe un empleado con el documento ingresado.");
        }
        else
            return true;
    }

    public String getNombreCompleto(Empleado empleado){
        String nombreCompleto = empleado.getNombre()+" "+empleado.getApellido();
        return nombreCompleto;
    }
}
