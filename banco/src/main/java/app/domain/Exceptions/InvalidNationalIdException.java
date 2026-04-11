package app.domain.Exceptions;

public class InvalidNationalIdException extends RuntimeException {
    public InvalidNationalIdException(String mensaje) {
        super(mensaje);
    }
}
