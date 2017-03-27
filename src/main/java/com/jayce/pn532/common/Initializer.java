package com.jayce.pn532.common;

import com.jayce.pn532.exception.InitializationException;

/**
 * Created by Jaycejia on 2017/3/26.
 */
public interface Initializer<T> {
    T initialize() throws InitializationException;
}
