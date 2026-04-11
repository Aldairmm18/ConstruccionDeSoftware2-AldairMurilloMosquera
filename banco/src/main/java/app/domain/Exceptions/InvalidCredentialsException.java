package app.domain.Exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String mensaje) {
        super(mensaje);
    }
}
