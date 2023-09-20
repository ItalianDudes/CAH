package it.italiandudes.cah.exceptions.connection;

@SuppressWarnings("unused")
public class InvalidAddressException extends IllegalArgumentException {
    public InvalidAddressException(String message) {
        super(message);
    }
    public InvalidAddressException(Throwable cause) {
        super(cause);
    }
    public InvalidAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
