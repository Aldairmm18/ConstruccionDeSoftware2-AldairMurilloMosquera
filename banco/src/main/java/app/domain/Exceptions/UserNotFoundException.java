package app.domain.Exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String mensaje) {
        super(mensaje);
    }
}
