
package com.jayce.raspi.rfid.nfc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jayce.raspi.rfid.enu.CommandStatus;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.wiringpi.Gpio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PN532I2C implements IPN532 {
    private static final Logger logger = LoggerFactory.getLogger(PN532I2C.class);

    private I2CDevice i2cDevice;
    private boolean debugReads = false;

    private byte command;

    private static final int DEVICE_ADDRESS = 0x24;

    @Override
    public void begin(int bus) throws I2CFactory.UnsupportedBusNumberException, IOException, InterruptedException {
            I2CBus i2cBus = I2CFactory.getInstance(bus);
            logger.debug("成功连接总线!!!");

            i2cDevice = i2cBus.getDevice(DEVICE_ADDRESS);
            logger.debug("成功连接读卡设备!!!");
            Thread.sleep(500);
    }

    @Override
    public void wakeup() {

    }

    @Override
    public CommandStatus writeCommand(byte[] header, byte[] body) throws InterruptedException {
        logger.debug("writing command(header: {}, body: {})", getByteString(header), getByteString(body));
        List<Byte> toSend = new ArrayList<>();

        command = header[0];
        try {
            toSend.add(PREAMBLE);
            toSend.add(START_CODE1);
            toSend.add(START_CODE2);

            byte cmdLength = (byte) header.length;
            cmdLength += (byte) body.length;
            cmdLength++;
            byte cmdLength1 = (byte) (~cmdLength + 1);

            toSend.add(cmdLength);
            toSend.add(cmdLength1);

            toSend.add(HOST_TO_PN532);

            byte sum = HOST_TO_PN532;

            for (byte aHeader : header) {
                toSend.add(aHeader);
                sum += aHeader;
            }

            for (byte aBody : body) {
                toSend.add(aBody);
                sum += aBody;
            }

            byte checksum = (byte) (~sum + 1);
            toSend.add(checksum);
            toSend.add(POST_AMBLE);
            byte[] bytesToSend = new byte[toSend.size()];
            for (int i = 0; i < bytesToSend.length; i++) {
                bytesToSend[i] = toSend.get(i);
            }
            logger.debug("pn532i2c.writeCommand sending " + getByteString(bytesToSend));
            i2cDevice.write(DEVICE_ADDRESS, bytesToSend, 0, bytesToSend.length);

        } catch (IOException e) {
            logger.error("pn532i2c.writeCommand exception occurred: ", e);
            return CommandStatus.INVALID_ACK;
        }
        logger.debug("pn532i2c.writeCommand transferring to waitForAck())");
        return waitForAck(5000);

    }

    private CommandStatus waitForAck(int timeout) throws InterruptedException {
        logger.debug("pn532i2c.waitForAck()");

        byte ackBuff[] = new byte[7];
        byte PN532_ACK[] = new byte[]{0, 0, (byte) 0xFF, 0, (byte) 0xFF, 0};

        int timer = 0;
        String message = "";
        while (true) {
            try {
                int read = i2cDevice.read(ackBuff, 0, 7);
                if (debugReads && read > 0) {
                    logger.debug("pn532i2c.waitForAck Read " + read + " bytes.");
                }
            } catch (IOException e) {
                message = e.getMessage();
            }

            if ((ackBuff[0] & 1) > 0) {
                break;
            }

            if (timeout != 0) {
                timer += 10;
                if (timer > timeout) {
                    logger.debug("pn532i2c.waitForAck timeout occurred: " + message);
                    return CommandStatus.TIMEOUT;
                }
            }
            Gpio.delay(10);

        }

        for (int i = 1; i < ackBuff.length; i++) {
            if (ackBuff[i] != PN532_ACK[i - 1]) {
                logger.warn("Invalid Ack.");
                return CommandStatus.INVALID_ACK;
            }
        }
        logger.debug("Ack OK");
        return CommandStatus.OK;

    }

    @Override
    public CommandStatus writeCommand(byte[] header) throws InterruptedException {
        return writeCommand(header, new byte[0]);
    }

    @Override
    public int readResponse(byte[] buffer, int expectedLength, int timeout) throws InterruptedException {
        logger.debug("pn532i2c.readResponse");

        byte response[] = new byte[expectedLength + 2];

        int timer = 0;

        while (true) {
            try {
                int read = i2cDevice.read(response, 0, expectedLength + 2);
                if (debugReads && read > 0) {
                    logger.debug("pn532i2c.waitForAck Read {} bytes.", read);
                }
            } catch (IOException e) {
                // Nothing, timeout will occur if an error has happened.
            }

            if ((response[0] & 1) > 0) {
                break;
            }

            if (timeout != 0) {
                timer += 10;
                if (timer > timeout) {
                    logger.debug("timeout occurred.");
                    return -1;
                }
            }
            Gpio.delay(10);

        }

        int ind = 1;

        if (PREAMBLE != response[ind++] || START_CODE1 != response[ind++] || START_CODE2 != response[ind++]) {
            logger.warn("response bad starting bytes found");
            return -1;
        }

        byte length = response[ind++];
        byte com_length = length;
        com_length += response[ind++];
        if (com_length != 0) {
            logger.warn("response bad length checksum");
            return -1;
        }

        byte cmd = 1;
        cmd += command;

        if (PN532_TO_HOST != response[ind++] || (cmd) != response[ind++]) {
            logger.warn("response bad command check.");
            return -1;
        }

        length -= 2;
        if (length > expectedLength) {
            logger.warn("response not enough space");
            return -1;
        }

        byte sum = PN532_TO_HOST;
        sum += cmd;

        for (int i = 0; i < length; i++) {
            buffer[i] = response[ind++];
            sum += buffer[i];
        }

        byte checksum = response[ind++];
        checksum += sum;
        if (0 != checksum) {
            logger.warn("response bad checksum");
            return -1;
        }

        return length;

    }

    @Override
    public int readResponse(byte[] buffer, int expectedLength) throws InterruptedException {
        return readResponse(buffer, expectedLength, 1000);
    }


    private String getByteString(byte[] arr) {
        return Arrays.toString(arr);
    }
}
