package com.jayce.raspi.rfid.common;

import com.jayce.raspi.rfid.exception.InitializationException;

/**
 * Created by Jaycejia on 2017/3/26.
 */
public interface Initializer<T> {
    T initialize() throws InitializationException;
}
