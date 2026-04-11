package app.domain.Exceptions;

public class TelefonoInvalidoException extends RuntimeException {
    public TelefonoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
