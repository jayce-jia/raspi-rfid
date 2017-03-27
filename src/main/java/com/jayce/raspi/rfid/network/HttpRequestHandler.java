package com.jayce.raspi.rfid.network;

import retrofit2.Retrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Jaycejia on 2017/3/27.
 */
public class HttpRequestHandler implements InvocationHandler{
    private Retrofit retrofit;

    public HttpRequestHandler(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        retrofit.create(proxy.getClass());
        return method.invoke(proxy, args);
    }
}
