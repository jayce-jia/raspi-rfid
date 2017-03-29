package com.jayce.raspi.rfid.network;

import com.jayce.raspi.rfid.common.Initializer;
import com.jayce.raspi.rfid.enu.SysConfig;
import com.jayce.raspi.rfid.exception.InitializationException;
import com.jayce.raspi.rfid.exception.NetworkInitializationException;
import com.jayce.raspi.rfid.util.PropertiesUtil;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Created by Jaycejia on 2017/3/27.
 */
public class NetworkInitializer implements Initializer<Retrofit> {

    @Override
    public Retrofit initialize() throws InitializationException {
        try {
            String serverUrl = (String) PropertiesUtil.getProperty(SysConfig.SERVER_IP);
            String serverPort = (String) PropertiesUtil.getProperty(SysConfig.SERVER_PORT);
            String context = (String) PropertiesUtil.getProperty(SysConfig.SERVER_CONTEXT);
            String baseUrl = serverUrl + "/" + serverPort + "/" + context + "/";
            OkHttpClient client = new OkHttpClient();
            return new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        } catch (IOException e) {
            throw new NetworkInitializationException("配置文件错误", e);
        }
    }
}
