package app.domain.Exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String mensaje) {
        super(mensaje);
    }
}
