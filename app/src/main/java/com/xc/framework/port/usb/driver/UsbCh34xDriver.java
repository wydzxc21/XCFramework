package com.xc.framework.port.usb.driver;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import java.io.IOException;

/**
 * @Date：2021/4/23
 * @Author：ZhangXuanChen
 * @Description：Qinheng CH34x, CH341A, CH340
 */
public class UsbCh34xDriver extends UsbBaseDriver {
    private static final int USB_TIMEOUT_MILLIS = 5000;
    private static final int LCR_ENABLE_RX = 0x80;
    private static final int LCR_ENABLE_TX = 0x40;
    private static final int LCR_MARK_SPACE = 0x20;
    private static final int LCR_PAR_EVEN = 0x10;
    private static final int LCR_ENABLE_PAR = 0x08;
    private static final int LCR_STOP_BITS_2 = 0x04;
    private static final int LCR_CS8 = 0x03;
    private static final int LCR_CS7 = 0x02;
    private static final int LCR_CS6 = 0x01;
    private static final int LCR_CS5 = 0x00;
    private static final int SCL_DTR = 0x20;
    private static final int SCL_RTS = 0x40;

    @Override
    protected void openInt() throws IOException {
        for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
            UsbInterface usbIface = mDevice.getInterface(i);
            if (!mConnection.claimInterface(usbIface, true)) {
                throw new IOException("Could not claim data interface");
            }
        }
        UsbInterface dataIface = mDevice.getInterface(mDevice.getInterfaceCount() - 1);
        for (int i = 0; i < dataIface.getEndpointCount(); i++) {
            UsbEndpoint ep = dataIface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                    mReadEndpoint = ep;
                } else {
                    mWriteEndpoint = ep;
                }
            }
        }
        initialize(baudRate);
        setBaudRate(baudRate);
        setParameters(baudRate, dataBits, stopBits, parity);
    }

    @Override
    protected void closeInt() {
        try {
            for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
                mConnection.releaseInterface(mDevice.getInterface(i));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：initialize
     */
    private void initialize(int baudRate) throws IOException {
        checkState("init #1", 0x5f, 0, new int[]{-1 /* 0x27, 0x30 */, 0x00});
        if (controlOut(0xa1, 0, 0) < 0) {
            throw new IOException("Init failed: #2");
        }
        setBaudRate(baudRate);
        checkState("init #4", 0x95, 0x2518, new int[]{-1 /* 0x56, c3*/, 0x00});
        if (controlOut(0x9a, 0x2518, LCR_ENABLE_RX | LCR_ENABLE_TX | LCR_CS8) < 0) {
            throw new IOException("Init failed: #5");
        }
        checkState("init #6", 0x95, 0x0706, new int[]{-1/*0xf?*/, -1/*0xec,0xee*/});
        if (controlOut(0xa1, 0x501f, 0xd90a) < 0) {
            throw new IOException("Init failed: #7");
        }
        setBaudRate(baudRate);
        setControlLines();
        checkState("init #10", 0x95, 0x0706, new int[]{-1/* 0x9f, 0xff*/, -1/*0xec,0xee*/});
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：checkState
     */
    private void checkState(String msg, int request, int value, int[] expected) throws IOException {
        byte[] buffer = new byte[expected.length];
        int ret = controlIn(request, value, 0, buffer);
        if (ret < 0) {
            throw new IOException("Failed send cmd [" + msg + "]");
        }
        if (ret != expected.length) {
            throw new IOException("Expected " + expected.length + " bytes, but get " + ret + " [" + msg + "]");
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] == -1) {
                continue;
            }
            int current = buffer[i] & 0xff;
            if (expected[i] != current) {
                throw new IOException("Expected 0x" + Integer.toHexString(expected[i]) + " byte, but get 0x" + Integer.toHexString(current) + " [" + msg + "]");
            }
        }
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：controlIn
     */
    private int controlIn(int request, int value, int index, byte[] buffer) {
        final int REQTYPE_DEVICE_TO_HOST = UsbConstants.USB_TYPE_VENDOR | UsbConstants.USB_DIR_IN;
        return mConnection.controlTransfer(REQTYPE_DEVICE_TO_HOST, request, value, index, buffer, buffer.length, USB_TIMEOUT_MILLIS);
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：controlOut
     */
    private int controlOut(int request, int value, int index) {
        final int REQTYPE_HOST_TO_DEVICE = UsbConstants.USB_TYPE_VENDOR | UsbConstants.USB_DIR_OUT;
        return mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, request, value, index, null, 0, USB_TIMEOUT_MILLIS);
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：setBaudRate
     */
    private void setBaudRate(int baudRate) throws IOException {
        final long CH341_BAUDBASE_FACTOR = 1532620800;
        final int CH341_BAUDBASE_DIVMAX = 3;
        long factor = CH341_BAUDBASE_FACTOR / baudRate;
        int divisor = CH341_BAUDBASE_DIVMAX;
        while ((factor > 0xfff0) && divisor > 0) {
            factor >>= 3;
            divisor--;
        }
        if (factor > 0xfff0) {
            throw new UnsupportedOperationException("Unsupported baud rate: " + baudRate);
        }
        factor = 0x10000 - factor;
        divisor |= 0x0080; // else ch341a waits until buffer full
        int ret = controlOut(0x9a, 0x1312, (int) ((factor & 0xff00) | divisor));
        if (ret < 0) {
            throw new IOException("Error setting baud rate: #1)");
        }
        ret = controlOut(0x9a, 0x0f2c, (int) (factor & 0xff));
        if (ret < 0) {
            throw new IOException("Error setting baud rate: #2");
        }
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：setControlLines
     */
    private void setControlLines() throws IOException {
        if (controlOut(0xa4, ~((dtr ? SCL_DTR : 0) | (rts ? SCL_RTS : 0)), 0) < 0) {
            throw new IOException("Failed to set control lines");
        }
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：setParameters
     */
    private void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        if (baudRate <= 0) {
            throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
        }
        setBaudRate(baudRate);
        int lcr = LCR_ENABLE_RX | LCR_ENABLE_TX;
        switch (dataBits) {
            case DATABITS_5:
                lcr |= LCR_CS5;
                break;
            case DATABITS_6:
                lcr |= LCR_CS6;
                break;
            case DATABITS_7:
                lcr |= LCR_CS7;
                break;
            case DATABITS_8:
                lcr |= LCR_CS8;
                break;
            default:
                throw new IllegalArgumentException("Invalid data bits: " + dataBits);
        }
        switch (parity) {
            case PARITY_NONE:
                break;
            case PARITY_ODD:
                lcr |= LCR_ENABLE_PAR;
                break;
            case PARITY_EVEN:
                lcr |= LCR_ENABLE_PAR | LCR_PAR_EVEN;
                break;
            case PARITY_MARK:
                lcr |= LCR_ENABLE_PAR | LCR_MARK_SPACE;
                break;
            case PARITY_SPACE:
                lcr |= LCR_ENABLE_PAR | LCR_MARK_SPACE | LCR_PAR_EVEN;
                break;
            default:
                throw new IllegalArgumentException("Invalid parity: " + parity);
        }
        switch (stopBits) {
            case STOPBITS_1:
                break;
            case STOPBITS_1_5:
                throw new UnsupportedOperationException("Unsupported stop bits: 1.5");
            case STOPBITS_2:
                lcr |= LCR_STOP_BITS_2;
                break;
            default:
                throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
        }
        int ret = controlOut(0x9a, 0x2518, lcr);
        if (ret < 0) {
            throw new IOException("Error setting control byte");
        }
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setBreak
     */
    public void setBreak(boolean value) throws IOException {
        byte[] req = new byte[2];
        if (controlIn(0x95, 0x1805, 0, req) < 0) {
            throw new IOException("Error getting BREAK condition");
        }
        if (value) {
            req[0] &= ~1;
            req[1] &= ~0x40;
        } else {
            req[0] |= 1;
            req[1] |= 0x40;
        }
        int val = (req[1] & 0xff) << 8 | (req[0] & 0xff);
        if (controlOut(0x9a, 0x1805, val) < 0) {
            throw new IOException("Error setting BREAK condition");
        }
    }
}
