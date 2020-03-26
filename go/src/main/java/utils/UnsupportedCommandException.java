package utils;

public class UnsupportedCommandException  extends RuntimeException {

    public UnsupportedCommandException() {
        // nop
    }

    public UnsupportedCommandException(String message) {
        super(message);
    }

}
