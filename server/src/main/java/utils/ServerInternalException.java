package utils;

public class ServerInternalException extends RuntimeException {

    public ServerInternalException() {
        // nop
    }

    public ServerInternalException(String message) {
        super(message);
    }

}
