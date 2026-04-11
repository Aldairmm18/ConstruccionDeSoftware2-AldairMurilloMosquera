package app.domain.Exceptions;

public class NITInvalidoException extends RuntimeException {
    public NITInvalidoException(String mensaje) {
        super(mensaje);
    }
}
