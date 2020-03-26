package hse.cs.networks.utils;

public class UnsupportedCommandException extends ServerInternalException {

    public UnsupportedCommandException() {
        // nop
    }

    public UnsupportedCommandException(String message) {
        super(message);
    }
}
