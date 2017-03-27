package com.jayce.raspi.rfid.nfc;

import com.jayce.raspi.rfid.enu.CommandStatus;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

public class PN532 {

    private static final byte PN532_COMMAND_GET_FIRMWARE_VERSION = 0x02;
    private static final byte PN532_COMMAND_SAM_CONFIGURATION = 0x14;
    private static final byte PN532_COMMAND_IN_LIST_PASSIVE_TARGET = 0x4A;

    private IPN532 medium;
    private byte[] PN532_PACKET_BUFFER;

    public PN532(IPN532 medium) {
        this.medium = medium;
        this.PN532_PACKET_BUFFER = new byte[64];
    }

    public void begin() throws I2CFactory.UnsupportedBusNumberException, IOException, InterruptedException {
        medium.begin(I2CBus.BUS_1);
        medium.wakeup();
    }

    public long getFirmwareVersion() throws InterruptedException {
        long response;

        byte[] command = new byte[1];
        command[0] = PN532_COMMAND_GET_FIRMWARE_VERSION;

        if (medium.writeCommand(command) != CommandStatus.OK) {
            return 0;
        }

        // read data packet
        int status = medium.readResponse(PN532_PACKET_BUFFER, 12);
        if (status < 0) {
            return 0;
        }

        int offset = 0; //medium.getOffsetBytes();

        response = PN532_PACKET_BUFFER[offset];
        response <<= 8;
        response |= PN532_PACKET_BUFFER[offset + 1];
        response <<= 8;
        response |= PN532_PACKET_BUFFER[offset + 2];
        response <<= 8;
        response |= PN532_PACKET_BUFFER[offset + 3];

        return response;
    }

    public boolean samConfig() throws InterruptedException {
        byte[] command = new byte[4];
        command[0] = PN532_COMMAND_SAM_CONFIGURATION;
        command[1] = 0x01; // normal mode;
        command[2] = 0x14; // timeout 50ms * 20 = 1 second
        command[3] = 0x01; // use IRQ pin!

        return medium.writeCommand(command) == CommandStatus.OK && medium.readResponse(PN532_PACKET_BUFFER, 8) > 0;

    }

    public int readPassiveTargetID(byte cardBaudRate, byte[] buffer) throws InterruptedException {
        byte[] command = new byte[3];
        command[0] = PN532_COMMAND_IN_LIST_PASSIVE_TARGET;
        command[1] = 1; // max 1 cards at once (we can set this to 2 later)
        command[2] = cardBaudRate;

        if (medium.writeCommand(command) != CommandStatus.OK) {
            return -1; // command failed
        }

        // read data packet
        if (medium.readResponse(PN532_PACKET_BUFFER, 20) < 0) {
            return -1;
        }

        // check some basic stuff
        /*
		 * ISO14443A card response should be in the following format:
		 * 
		 * byte Description -------------
		 * ------------------------------------------ b0 Tags Found b1 Tag
		 * Number (only one used in this example) b2..3 SENS_RES b4 SEL_RES b5
		 * NFCID Length b6..NFCIDLen NFCID
		 */

        int offset = 0; //medium.getOffsetBytes();

        if (PN532_PACKET_BUFFER[offset] != 1) {
            return -1;
        }

		/* Card appears to be Mifare Classic */
        int uidLength = PN532_PACKET_BUFFER[offset + 5];

        for (int i = 0; i < uidLength; i++) {
            buffer[i] = PN532_PACKET_BUFFER[offset + 6 + i];
        }

        return uidLength;
    }

}