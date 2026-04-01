package it.sara.demo.web;

import it.sara.demo.dto.StatusDTO;
import it.sara.demo.exception.GenericException;
import it.sara.demo.web.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestore globale delle eccezioni dell'applicazione.
 *
 * <p>Questa classe, annotata con {@link RestControllerAdvice}, intercetta le
 * eccezioni sollevate dai controller REST e restituisce risposte strutturate al client in formato {@link GenericResponse}.</p>
 *
 * <p>Gli scopi principali della classe sono:</p>
 * <ul>
 *     <li>Gestire in modo centralizzato tutte le eccezioni personalizzate {@link GenericException}</li>
 *     <li>Fornire una risposta generica per qualsiasi altra eccezione inattesa</li>
 *     <li>Garantire consistenza e uniformità nel formato delle risposte di errore</li>
 * </ul>
 *
 * <p>Le risposte vengono sempre restituite con HTTP 200, mentre il codice e la descrizione
 * dell’errore sono incapsulati all'interno dello {@link StatusDTO} contenuto nel body.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce le eccezioni personalizzate di tipo {@link GenericException}.
     *
     * <p>Estrae lo {@link StatusDTO} contenuto nell'eccezione e lo inserisce
     * nella risposta JSON. Questo permette ai servizi di generare errori applicativi specifici e controllati.</p>
     *
     * @param ex l'eccezione generata dal servizio
     * @return un {@link ResponseEntity} contenente un {@link GenericResponse} popolato con le informazioni di errore provenienti dall’eccezione
     */
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GenericResponse> handleGenericException(GenericException ex) {

        StatusDTO status = ex.getStatus();
        GenericResponse response = new GenericResponse();
        response.setStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Gestisce tutte le eccezioni generiche non intercettate da altri handler.
     *
     * <p>Utilizza lo status predefinito {@link GenericException#GENERIC_ERROR}
     * che rappresenta un errore imprevisto lato server.</p>
     *
     * @param ex eccezione sconosciuta o non gestita esplicitamente
     * @return un {@link ResponseEntity} contenente un {@link GenericResponse} con uno stato di errore generico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleException(Exception ex) {

        GenericResponse response = new GenericResponse();
        response.setStatus(GenericException.GENERIC_ERROR);
        return ResponseEntity.ok(response);
    }
}