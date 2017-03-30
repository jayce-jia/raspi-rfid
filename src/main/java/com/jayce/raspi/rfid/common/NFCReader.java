package com.jayce.raspi.rfid.common;

import com.jayce.raspi.rfid.nfc.PN532;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jaycejia on 2017/3/28.
 */
public class NFCReader {
    private static final Logger logger = LoggerFactory.getLogger(NFCReader.class);
    private static final byte PN532_MIFARE_ISO14443A = 0x00;
    private PN532 nfc;

    public NFCReader(PN532 nfc) {
        this.nfc = nfc;
    }

    public String readCardUID() {
        byte[] buffer = new byte[8];
        try {
            int readLength = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A,
                    buffer);
            if (readLength > 0) {
                logger.debug("Found an ISO14443A card");
                StringBuilder uid = new StringBuilder();
                for (int i = 0; i < readLength; i++) {
                    uid.append(Integer.toHexString(buffer[i]));
                }
                logger.debug("UID Length: {} bytes  UID Value: [{}]", readLength, uid);
                return uid.toString();
            }
        } catch (InterruptedException e) {
            //Ignored
        }
        return null;
    }
}
