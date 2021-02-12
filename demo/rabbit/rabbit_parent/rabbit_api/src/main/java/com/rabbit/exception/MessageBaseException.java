package com.rabbit.exception;

public class MessageBaseException extends Exception{
    private static final long serialVersionUID = -3910705859496279633L;

    public MessageBaseException() {
        super();
    }

    public MessageBaseException(String message) {
        super(message);
    }

    public MessageBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageBaseException(Throwable cause) {
        super(cause);
    }
}
