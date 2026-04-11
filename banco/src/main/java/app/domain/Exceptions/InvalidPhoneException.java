package app.domain.Exceptions;

public class InvalidPhoneException extends RuntimeException {
    public InvalidPhoneException(String mensaje) {
        super(mensaje);
    }
}
