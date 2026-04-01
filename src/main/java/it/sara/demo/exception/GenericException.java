package it.sara.demo.exception;

import it.sara.demo.dto.StatusDTO;
import lombok.Getter;

/**
 * Eccezione personalizzata utilizzata per rappresentare errori applicativi
 * all'interno del sistema.
 *
 * <p>Questa classe estende {@link RuntimeException} e incapsula un oggetto
 * {@link StatusDTO} contenente:</p>
 *
 * <ul>
 *     <li><b>code</b>: il codice numerico dell’errore</li>
 *     <li><b>message</b>: la descrizione dell’errore</li>
 *     <li><b>traceId</b>: un identificatore univoco utile per il tracciamento</li>
 * </ul>
 *
 * <p>Sono inoltre disponibili due costanti predefinite:</p>
 * <ul>
 *     <li>{@link #GENERIC_ERROR} – errore generico (codice 500)</li>
 *     <li>{@link #NOT_FOUND} – risorsa non trovata (codice 404)</li>
 * </ul>
 *
 * <p>La classe viene utilizzata insieme al {@code GlobalExceptionHandler} per
 * restituire errori strutturati al client in formato standard.</p>
 */

@Getter
public class GenericException extends RuntimeException {

    /**
     * Stato predefinito utilizzato per rappresentare un errore generico (500).
     */
    public final static StatusDTO GENERIC_ERROR = new StatusDTO();

    /**
     * Stato predefinito utilizzato quando una risorsa non viene trovata (404).
     */

    public final static StatusDTO NOT_FOUND = new StatusDTO();

    public final static StatusDTO UNAUTHORIZED = new StatusDTO();

    public final static StatusDTO BAD_REQUEST = new StatusDTO();

    static {
        GENERIC_ERROR.setCode(500);
        GENERIC_ERROR.setMessage("Generic error");
        NOT_FOUND.setCode(404);
        NOT_FOUND.setMessage("Not Found");
        UNAUTHORIZED.setCode(401);
        UNAUTHORIZED.setMessage("Unauthorized");
        BAD_REQUEST.setCode(400);
        BAD_REQUEST.setMessage("Bad Request");
    }


    /**
     * Stato associato all'eccezione lanciata.
     */
    private final StatusDTO status;


    /**
     * Costruisce una nuova eccezione basata su uno {@link StatusDTO} già definito.
     *
     * @param status lo stato da associare all'eccezione
     */
    public GenericException(StatusDTO status) {
        this.status = status;
    }

    /**
     * Costruisce una nuova eccezione a partire da un codice e da un messaggio.
     *
     * <p>Viene generato automaticamente anche un {@code traceId} per facilitare
     * il monitoraggio e la diagnosi dei problemi.</p>
     *
     * @param code il codice numerico dell'errore
     * @param message la descrizione dell'errore
     */
    public GenericException(int code, String message) {
        this.status = createStatus(code, message);
    }

    /**
     * Crea e popola un nuovo {@link StatusDTO} associando:
     * <ul>
     *     <li>il codice</li>
     *     <li>il messaggio</li>
     *     <li>un identificatore univoco {@code traceId}</li>
     * </ul>
     *
     * @param code il codice dell’errore
     * @param message la descrizione dell’errore
     * @return un oggetto {@link StatusDTO} completamente popolato
     */
    private StatusDTO createStatus(int code, String message) {
        StatusDTO status = new StatusDTO();
        status.setCode(code);
        status.setMessage(message);
        status.setTraceId(java.util.UUID.randomUUID().toString());
        return status;
    }
}
