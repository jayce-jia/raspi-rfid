package com.jayce.raspi.rfid.enu;

/**
 * Created by Jaycejia on 2017/3/25.
 */
public enum SysConfig {
    SERVER_IP("服务器IP地址", "server.url"),
    SERVER_PORT("服务器端口号", "server.port"),
    SERVER_CONTEXT("服务器项目名称", "server.context"),

    NFC_POLL_INTERVAL("轮询NFC间隔(ms)", "nfc.poll.interval"),
    NFC_POLL_INVALID_TIMEOUT("NFC读取成功后下一次读取相同tag的间隔(ms)", "nfc.poll.invalid.timeout"),
    NFC_READER_ID("冰箱NFC模块id", "nfc.reader.sn");


    private String confName;
    private String confKey;

    SysConfig(String confName, String confKey) {
        this.confName = confName;
        this.confKey = confKey;
    }

    public String getConfName() {
        return confName;
    }

    public String getConfKey() {
        return confKey;
    }
}
