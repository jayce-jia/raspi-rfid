package com.jayce.raspi.rfid.util;

import com.jayce.raspi.rfid.enu.SysConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Jaycejia on 2017/3/25.
 */
public class PropertiesUtil {
    private static final String PROP_FILE_NAME = "config.properties";
    private static ThreadLocal<Properties> properties = new ThreadLocal<>();

    public static Object getProperty(SysConfig key) throws IOException {
        ClassLoader classLoader = PropertiesUtil.class.getClassLoader();
        InputStream file = classLoader.getResourceAsStream(PROP_FILE_NAME);
        if (file == null) {
            return null;
        }
        Properties config = properties.get();
        if (config == null) {
            config = new Properties();
            properties.set(config);
        }
        config.load(file);
        return config.get(key.getConfKey());
    }
}
