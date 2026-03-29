package it.sara.demo.web;

import it.sara.demo.dto.StatusDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.web.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GenericResponse> handleGenericException(GenericException ex) {

        StatusDTO status = ex.getStatus();
        GenericResponse response = new GenericResponse();
        response.setStatus(status);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleException(Exception ex) {

        GenericResponse response = new GenericResponse();
        response.setStatus(GenericException.GENERIC_ERROR);
        return ResponseEntity.ok(response);
    }
}