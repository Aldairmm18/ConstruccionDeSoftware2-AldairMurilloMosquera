package app.domain.Exceptions;

public class TransferExpiredException extends RuntimeException {
    public TransferExpiredException(String mensaje) {
        super(mensaje);
    }
}
