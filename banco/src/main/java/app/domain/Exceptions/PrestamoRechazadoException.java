package app.domain.Exceptions;

public class PrestamoRechazadoException extends RuntimeException {
    public PrestamoRechazadoException(String mensaje) {
        super(mensaje);
    }
}
