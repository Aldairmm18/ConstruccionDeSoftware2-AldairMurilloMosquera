package app.domain.Exceptions;

public class InvalidNitException extends RuntimeException {
    public InvalidNitException(String mensaje) {
        super(mensaje);
    }
}
