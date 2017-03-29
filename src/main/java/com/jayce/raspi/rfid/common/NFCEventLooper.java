package com.jayce.raspi.rfid.common;

import com.jayce.raspi.rfid.nfc.PN532;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.subjects.PublishSubject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jaycejia on 2017/3/28.
 */
public class NFCEventLooper {
    private static final Logger logger = LoggerFactory.getLogger(NFCEventLooper.class);
    private static final byte PN532_MIFARE_ISO14443A = 0x00;
    private PN532 nfc;
    private PublishSubject<String> subject;

    private AtomicBoolean loopOn = new AtomicBoolean(true);

    public NFCEventLooper(PN532 nfc) {
        this.nfc = nfc;
    }

    public void startLoop() {
        logger.info("Waiting for an ISO14443A Card ...");
        byte[] buffer = new byte[8];
        try {
            while (loopOn.get()) {
                int readLength = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A,
                        buffer);
                if (readLength > 0) {
                    logger.debug("Found an ISO14443A card");
                    StringBuilder uid = new StringBuilder();
                    for (int i = 0; i < readLength; i++) {
                        uid.append(Integer.toHexString(buffer[i]));
                    }
                    logger.info("UID Length: {} bytes  UID Value: [{}]", readLength, uid);
                    subject.onNext(uid.toString());
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("loop中断", e);
        }
    }

    public PublishSubject<String> init() {
        this.subject = PublishSubject.create();
        return subject;
    }

    public void stopLoop() {
        loopOn.set(false);
    }


}
