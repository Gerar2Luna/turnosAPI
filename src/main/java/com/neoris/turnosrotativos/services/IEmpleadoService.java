package com.neoris.turnosrotativos.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.neoris.turnosrotativos.entities.Empleado;

public interface IEmpleadoService {
    
    public List<Empleado> obtenerEmpleados();
    public ResponseEntity<Object> crearEmpleado(@Valid Empleado empleado);
    public Optional<Empleado> obtenerInfoEmpleado(Integer id);
    public ResponseEntity<Empleado> actualizarEmpleado(@Valid Empleado empleadoActualizado, @PathVariable Integer id);
    public ResponseEntity<Object> eliminarEmpleadoPorId(@PathVariable Integer id);
    public boolean empleadoExists(Integer empleadoID);
    public void validarCreacionEmpleado(Empleado empleado);
    public void validarActualizarEmpleado(Empleado empleado, Integer id);
    public boolean validarEdad(String fechaNacimiento);
    public boolean validarFechaNacimiento(String fechaNacimiento);
    public boolean validarFechaIngreso(String fechaIngreso);
    public boolean validarFormatoNombre(String nombre, String apellido);
    public boolean validarDocumento(Integer nroDocumento);
    public boolean validarFormatoEmail(String email);
    public boolean validarEmailDuplicado(String email);
    public boolean validarActualizacionEmail(String email, Integer id);
    public boolean validarActualizacionDocumento(Integer nroDocumento, Integer id);
    public String getNombreCompleto(Empleado empleado);
} 
