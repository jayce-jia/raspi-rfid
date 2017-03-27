package com.jayce.raspi.rfid.exception;

/**
 * Created by Jaycejia on 2017/3/27.
 */
public class NetworkInitializationException extends InitializationException {
    public NetworkInitializationException() {
    }

    public NetworkInitializationException(String message) {
        super(message);
    }

    public NetworkInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkInitializationException(Throwable cause) {
        super(cause);
    }

    public NetworkInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
