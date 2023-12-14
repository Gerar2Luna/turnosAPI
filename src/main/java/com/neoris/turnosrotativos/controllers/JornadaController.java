package com.neoris.turnosrotativos.controllers;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.neoris.turnosrotativos.services.JornadaService;
import com.neoris.turnosrotativos.entities.Jornada;
import com.neoris.turnosrotativos.entities.dtos.JornadaDTO;

@RestController
@RequestMapping("/jornada")
public class JornadaController  {
    @Autowired
    JornadaService jornadaService;
    
    @PostMapping
    public ResponseEntity<Jornada> crearJornada (@Valid @RequestBody JornadaDTO jornadaDTO){
        return new ResponseEntity<Jornada>(jornadaService.crearJornada(jornadaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Jornada>> enlistarJornadas(
      @RequestParam(value = "nroDocumento", required = false) Integer nroDocumento,
      @RequestParam(value = "fecha", required = false) String fecha){
        if(nroDocumento!=null && fecha!=null){ // Se evaluan que ambos tengan valor y se invoca al metodo que filtra por ambos parametros
                return new ResponseEntity<List<Jornada>>(jornadaService.getByNroDocumentoAndFecha(nroDocumento,fecha), HttpStatus.OK);
        } else if (nroDocumento!=null){ // Se evalua individualmente que alguno tenga un valor y se ejecuta la consulta correspondiente
            return new ResponseEntity<List<Jornada>>(jornadaService.getByNroDocumento(nroDocumento), HttpStatus.OK);
        } else if (fecha!=null){
            return new ResponseEntity<List<Jornada>>(jornadaService.getByFecha(fecha), HttpStatus.OK);  
        } else  // Si se llego a este punto quiere decir que ni ambos ni alguno es distinto a null por lo tanto solo ambos pueden ser null
            return new ResponseEntity<List<Jornada>>(jornadaService.getAllJornadas(), HttpStatus.OK);
            
        }
    
}
