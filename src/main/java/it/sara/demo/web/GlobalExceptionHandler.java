package it.sara.demo.web;

import it.sara.demo.dto.StatusDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.web.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestore globale delle eccezioni per l'applicazione.
 * Intercetta le eccezioni di tipo GenericException e le eccezioni generiche, restituendo una risposta standardizzata con lo stato dell'errore.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce le eccezioni di tipo GenericException, restituendo una risposta con lo stato specifico dell'errore.
     *
     * @param ex l'eccezione di tipo GenericException intercettata
     * @return una risposta HTTP con un oggetto GenericResponse contenente lo stato dell'errore
     */
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GenericResponse> handleGenericException(GenericException ex) {

        StatusDTO status = ex.getStatus();
        GenericResponse response = new GenericResponse();
        response.setStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Gestisce tutte le eccezioni generiche, restituendo una risposta con uno stato di errore generico.
     *
     * @param ex l'eccezione generica intercettata
     * @return una risposta HTTP con un oggetto GenericResponse contenente uno stato di errore generico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleException(Exception ex) {

        GenericResponse response = new GenericResponse();
        response.setStatus(GenericException.GENERIC_ERROR);
        return ResponseEntity.ok(response);
    }
}