package com.jayce.raspi.rfid.exception;

/**
 * Created by Jaycejia on 2017/3/26.
 */
public class NFCInitializationException extends InitializationException {
    public NFCInitializationException() {
    }

    public NFCInitializationException(String message) {
        super(message);
    }

    public NFCInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NFCInitializationException(Throwable cause) {
        super(cause);
    }

    public NFCInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
