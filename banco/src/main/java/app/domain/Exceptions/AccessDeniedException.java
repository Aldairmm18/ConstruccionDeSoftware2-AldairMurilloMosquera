package app.domain.Exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String mensaje) {
        super(mensaje);
    }
}
