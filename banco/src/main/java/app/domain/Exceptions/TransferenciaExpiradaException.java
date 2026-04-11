package app.domain.Exceptions;

public class TransferenciaExpiradaException extends RuntimeException {
    public TransferenciaExpiradaException(String mensaje) {
        super(mensaje);
    }
}
