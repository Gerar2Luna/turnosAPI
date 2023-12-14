package com.neoris.turnosrotativos.exceptions;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Esta notacion permite que capture las excepciones
@RestControllerAdvice
public class CustomExceptionHandler {
    
    // Manejo de @valid excpetions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    StringBuilder message = new StringBuilder();

    for (int i = 0; i < fieldErrors.size(); i++) {
        FieldError fieldError = fieldErrors.get(i);
        String fieldName = fieldError.getField();
        

        message.append(fieldName).append(" es obligatorio.");

        // Si hay más errores, agregar un salto de línea
        if (i < fieldErrors.size() - 1) {
            message.append("\n");
        }
    }

        return message.toString();
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RecursoNoEncontradoException.class)
        public String handleRecursoNoEncontradoException(RecursoNoEncontradoException ex) {
            return ex.getMessage();
        }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictoException.class)
        public String handleConflictoException(ConflictoException ex) {
            return ex.getMessage();
        }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
        public String handleBadRequestException(BadRequestException ex) {
            return ex.getMessage();
        }


}
