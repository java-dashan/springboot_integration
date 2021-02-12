package com.rabbit.exception;

public class MessageRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 5271026511706604767L;

    public MessageRuntimeException() {
        super();
    }

    public MessageRuntimeException(String message) {
        super(message);
    }

    public MessageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRuntimeException(Throwable cause) {
        super(cause);
    }
}
