package app.domain.Exceptions;

public class IdentificacionDuplicadaException extends RuntimeException {
    public IdentificacionDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
