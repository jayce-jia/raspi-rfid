package com.jayce.raspi.rfid;

import com.jayce.raspi.rfid.common.Initializer;
import com.jayce.raspi.rfid.common.NFCEventLooper;
import com.jayce.raspi.rfid.network.NetworkInitializer;
import com.jayce.raspi.rfid.nfc.PN532Initializer;
import com.jayce.raspi.rfid.exception.InitializationException;
import com.jayce.raspi.rfid.nfc.PN532;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import rx.Scheduler;
import rx.internal.schedulers.NewThreadScheduler;
import rx.subjects.PublishSubject;

import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        NFCEventLooper looper = new NFCEventLooper(nfc);
        PublishSubject<String> subject = looper.init();
        subject.subscribe(uid -> logger.info("收到卡号回调：{}", uid));
        looper.startLoop();
    }
}