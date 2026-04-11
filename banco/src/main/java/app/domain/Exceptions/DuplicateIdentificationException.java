package app.domain.Exceptions;

public class DuplicateIdentificationException extends RuntimeException {
    public DuplicateIdentificationException(String mensaje) {
        super(mensaje);
    }
}
