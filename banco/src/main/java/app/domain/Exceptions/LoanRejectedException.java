package app.domain.Exceptions;

public class LoanRejectedException extends RuntimeException {
    public LoanRejectedException(String mensaje) {
        super(mensaje);
    }
}
