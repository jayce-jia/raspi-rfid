package com.jayce.raspi.rfid.nfc;

import com.jayce.raspi.rfid.common.Initializer;
import com.jayce.raspi.rfid.exception.InitializationException;
import com.jayce.raspi.rfid.exception.NFCInitializationException;
import com.pi4j.io.i2c.I2CFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Jaycejia on 2017/3/25.
 */
public class PN532Initializer implements Initializer<PN532>{
    private static final Logger logger = LoggerFactory.getLogger(PN532Initializer.class);

    @Override
    public PN532 initialize() throws InitializationException{
        try {
            IPN532 pn532 = new PN532I2C();
            PN532 nfc = new PN532(pn532);
            // Start
            logger.debug("RFID启动中...");
            nfc.begin();
            long version = nfc.getFirmwareVersion();
            if (version == 0) {
                logger.warn("未找到RFID设备 PN53x board");
                throw new NFCInitializationException("未找到RFID设备");
            }
            // Got ok data, print it out!
            logger.debug("Found chip PN5 {}", Long.toHexString((version >> 24) & 0xFF));
            logger.debug("Firmware ver. {}.{}", Long.toHexString((version >> 16) & 0xFF), Long.toHexString((version >> 8) & 0xFF));
            nfc.samConfig();
            return nfc;
        } catch (I2CFactory.UnsupportedBusNumberException | IOException | InterruptedException e) {
           throw new NFCInitializationException("启动RFID设备失败", e);
        }
    }
}
