package com.jayce.raspi.rfid;

import com.jayce.raspi.rfid.common.NFCReader;
import com.jayce.raspi.rfid.exception.InitializationException;
import com.jayce.raspi.rfid.network.NetworkInitializer;
import com.jayce.raspi.rfid.nfc.PN532;
import com.jayce.raspi.rfid.nfc.PN532Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        PN532 nfc = null;
        Retrofit retrofit = null;
        try {
            logger.info("初始化进程启动...");
            logger.info("初始化RFID中...");
            nfc = new PN532Initializer().initialize();
            retrofit = new NetworkInitializer().initialize();
            logger.info("RFID初始化完成！");
        } catch (InitializationException e) {
            logger.error("初始化失败!", e);
            System.exit(0);
        }
        NFCReader reader = new NFCReader(nfc);
        logger.info("Waiting for an ISO14443A Card ...");
        Observable.interval(100L, TimeUnit.MILLISECONDS, Schedulers.immediate())
                .map(interval -> reader.readCardUID())
                .subscribe(uid -> {
                    if (uid != null) {
                        logger.info("读到卡号：{}", uid);
                    }
                });
    }
}