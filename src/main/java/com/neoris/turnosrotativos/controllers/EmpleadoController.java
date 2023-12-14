package com.neoris.turnosrotativos.controllers;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.neoris.turnosrotativos.entities.Empleado;
import com.neoris.turnosrotativos.exceptions.BadRequestException;
import com.neoris.turnosrotativos.exceptions.RecursoNoEncontradoException;
import com.neoris.turnosrotativos.services.EmpleadoService;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/empleado")
public class EmpleadoController {
    @Autowired
    EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Empleado>> obtenerEmpleados(){
        return new ResponseEntity<List<Empleado>>(empleadoService.obtenerEmpleados(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerInformacionEmpleado(@PathVariable Integer id){
        if (empleadoService.empleadoExists(id)){
            return new ResponseEntity<>(empleadoService.obtenerInfoEmpleado(id).get(), HttpStatus.OK);
        } else {
            throw new RecursoNoEncontradoException("No se encontró el empleado con Id: " + id);
        }
    }

    @PostMapping()
    public ResponseEntity<Object> crearEmpleado(@Valid @RequestBody Empleado empleado){
        return empleadoService.crearEmpleado(empleado);
    }

    @CrossOrigin(origins="http://localhost:4200")
    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizarEmpleadoPorId(@Valid @RequestBody Empleado empleadoActualizado, @PathVariable Integer id){
        return empleadoService.actualizarEmpleado(empleadoActualizado, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarEmpleadoPorId(@PathVariable Integer id){
        return empleadoService.eliminarEmpleadoPorId(id);
    }

   
}
