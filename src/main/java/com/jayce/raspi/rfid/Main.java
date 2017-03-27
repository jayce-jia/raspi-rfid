package com.jayce.raspi.rfid;

import com.jayce.raspi.rfid.common.Initializer;
import com.jayce.raspi.rfid.nfc.PN532Initializer;
import com.jayce.raspi.rfid.exception.InitializationException;
import com.jayce.raspi.rfid.nfc.PN532;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final byte PN532_MIFARE_ISO14443A = 0x00;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        PN532 nfc = null;
        try {
            logger.info("初始化进程启动...");
            logger.info("初始化RFID中...");
            Initializer<PN532> initializer = new PN532Initializer();
            nfc = initializer.initialize();
            logger.info("RFID初始化完成！");
        } catch (InitializationException e) {
            logger.error("初始化失败!", e);
            System.exit(0);
        }
        byte[] buffer = new byte[8];
        logger.info("Waiting for an ISO14443A Card ...");
        while (true) {
            int readLength = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A,
                    buffer);
            if (readLength > 0) {
                logger.info("Found an ISO14443A card");
                StringBuilder uid = new StringBuilder();
                for (int i = 0; i < readLength; i++) {
                    uid.append(Integer.toHexString(buffer[i]));
                }
                logger.info("UID Length: {} bytes  UID Value: [{}]", readLength, uid);
            }
            Thread.sleep(100);
        }
    }
}