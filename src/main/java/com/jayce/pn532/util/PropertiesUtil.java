package com.jayce.pn532.util;

import com.jayce.pn532.enu.SysConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Jaycejia on 2017/3/25.
 */
public class PropertiesUtil {
    private static final String PROP_FILE_NAME = "config-properties";
    private static ThreadLocal<Properties> properties = new ThreadLocal<>();

    public static Object getProperty(SysConfig key) throws IOException {
        ClassLoader classLoader = PropertiesUtil.class.getClassLoader();
        URL url = classLoader.getResource(PROP_FILE_NAME);
        if (url == null) {
            return null;
        }
        Properties config = properties.get();
        config.load(new FileInputStream(url.getFile()));
        return config.get(key.getConfKey());
    }
}
