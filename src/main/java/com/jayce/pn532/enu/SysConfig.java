package com.jayce.pn532.enu;

/**
 * Created by Jaycejia on 2017/3/25.
 */
public enum SysConfig {
    SERVER_IP("服务器IP地址","server.ip"),
    SERVER_PORT("服务器端口号", 80);


    private String confName;
    private Object confKey;

    SysConfig(String confName, Object confKey) {
        this.confName = confName;
        this.confKey = confKey;
    }

    public String getConfName() {
        return confName;
    }

    public Object getConfKey() {
        return confKey;
    }
}
