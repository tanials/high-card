package it.sara.demo.exception;

import it.sara.demo.dto.StatusDTO;
import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

    public final static StatusDTO GENERIC_ERROR = new StatusDTO();
    public final static StatusDTO NOT_FOUND = new StatusDTO();

    static {
        GENERIC_ERROR.setCode(500);
        GENERIC_ERROR.setMessage("Generic error");
        NOT_FOUND.setCode(404);
        NOT_FOUND.setMessage("Not Found");
    }


    private final StatusDTO status;

    public GenericException(StatusDTO status) {
        this.status = status;
    }

    public GenericException(int code, String message) {
        this.status = createStatus(code, message);
    }

    private StatusDTO createStatus(int code, String message) {
        StatusDTO status = new StatusDTO();
        status.setCode(code);
        status.setMessage(message);
        status.setTraceId(java.util.UUID.randomUUID().toString());
        return status;
    }
}
