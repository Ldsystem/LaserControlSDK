package cn.brk2outside.laser.exception;

@SuppressWarnings("unused")
public class UnsupportedCmdException extends RuntimeException {
    public UnsupportedCmdException() {
        super();
    }

    public UnsupportedCmdException(String message) {
        super(message);
    }
}
