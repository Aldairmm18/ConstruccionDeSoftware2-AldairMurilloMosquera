package app.domain.Exceptions;

public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException(String mensaje) {
        super(mensaje);
    }
}
