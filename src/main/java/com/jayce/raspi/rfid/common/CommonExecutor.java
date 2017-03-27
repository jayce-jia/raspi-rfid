package com.jayce.raspi.rfid.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jaycejia on 2017/3/25.
 */
public class CommonExecutor {
    public static ExecutorService getExecutor() {
        return ExecutorHolder.executor;
    }

    private CommonExecutor() {
    }

    private static class ExecutorHolder {
        private static final ExecutorService executor = new ThreadPoolExecutor(3,
                5, 2000, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>());
    }

}
