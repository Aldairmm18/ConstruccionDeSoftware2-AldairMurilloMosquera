package app.domain.Exceptions;

public class CedulaInvalidaException extends RuntimeException {
    public CedulaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
