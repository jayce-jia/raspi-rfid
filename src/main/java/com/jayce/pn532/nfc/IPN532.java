package com.jayce.pn532.nfc;

import com.jayce.pn532.enu.CommandStatus;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

public interface IPN532 {

    byte PREAMBLE = 0x00;
    byte START_CODE1 = 0x00;
    byte START_CODE2 = (byte) 0xFF;
    byte POST_AMBLE = 0x00;
    byte HOST_TO_PN532 = (byte) 0xD4;
    byte PN532_TO_HOST = (byte) 0xD5;

    void begin(int bus) throws I2CFactory.UnsupportedBusNumberException, IOException, InterruptedException;

    void wakeup();

    CommandStatus writeCommand(byte[] header, byte[] body)
            throws InterruptedException;

    CommandStatus writeCommand(byte header[]) throws InterruptedException;

    int readResponse(byte[] buffer, int expectedLength,
                                     int timeout) throws InterruptedException;

    int readResponse(byte[] buffer, int expectedLength)
            throws InterruptedException;


}