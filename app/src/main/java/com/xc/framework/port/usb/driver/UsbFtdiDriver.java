package com.xc.framework.port.usb.driver;

import android.hardware.usb.UsbConstants;
import android.util.Log;

import java.io.IOException;

/**
 * @Date：2021/4/26
 * @Author：ZhangXuanChen
 * @Description：FTDI FT232R, FT2232H, FT4232H, FT232H, FT230X, FT231X, FT234XD
 */
public class UsbFtdiDriver extends UsbBaseDriver {
    public static final String TAG = "UsbFtdiDriver";
    private static final int READ_HEADER_LENGTH = 2; //contains MODEM_STATUS
    private static final int REQTYPE_HOST_TO_DEVICE = UsbConstants.USB_TYPE_VENDOR | UsbConstants.USB_DIR_OUT;
    //
    private static final int RESET_REQUEST = 0;
    private static final int MODEM_CONTROL_REQUEST = 1;
    private static final int SET_BAUD_RATE_REQUEST = 3;
    private static final int SET_DATA_REQUEST = 4;
    //
    private static final int MODEM_CONTROL_DTR_ENABLE = 0x0101;
    private static final int MODEM_CONTROL_DTR_DISABLE = 0x0100;
    private static final int MODEM_CONTROL_RTS_ENABLE = 0x0202;
    private static final int MODEM_CONTROL_RTS_DISABLE = 0x0200;
    private static final int RESET_ALL = 0;
    //
    private boolean baudRateWithPort = false;
    private int breakConfig = 0;

    @Override
    protected void openInt() throws IOException {
        if (!mConnection.claimInterface(mDevice.getInterface(mPortNumber), true)) {
            throw new IOException("Could not claim interface " + mPortNumber);
        }
        if (mDevice.getInterface(mPortNumber).getEndpointCount() < 2) {
            throw new IOException("Not enough endpoints");
        }
        mReadEndpoint = mDevice.getInterface(mPortNumber).getEndpoint(0);
        mWriteEndpoint = mDevice.getInterface(mPortNumber).getEndpoint(1);
        int result = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, RESET_REQUEST, RESET_ALL,
                mPortNumber + 1, null, 0, USB_TIMEOUT_MILLIS);
        if (result != 0) {
            throw new IOException("Reset failed: result=" + result);
        }
        result = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, MODEM_CONTROL_REQUEST,
                (dtr ? MODEM_CONTROL_DTR_ENABLE : MODEM_CONTROL_DTR_DISABLE) |
                        (rts ? MODEM_CONTROL_RTS_ENABLE : MODEM_CONTROL_RTS_DISABLE),
                mPortNumber + 1, null, 0, USB_TIMEOUT_MILLIS);
        if (result != 0) {
            throw new IOException("Init RTS,DTR failed: result=" + result);
        }
        byte[] rawDescriptors = mConnection.getRawDescriptors();
        if (rawDescriptors == null || rawDescriptors.length < 14) {
            throw new IOException("Could not get device descriptors");
        }
        int deviceType = rawDescriptors[13];
        baudRateWithPort = deviceType == 7 || deviceType == 8 || deviceType == 9; // ...H devices
        setParameters(baudRate, dataBits, stopBits, parity);
    }

    @Override
    protected void closeInt() {
        try {
            mConnection.releaseInterface(mDevice.getInterface(mPortNumber));
        } catch (Exception ignored) {
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
        setBaudrate(baudRate);
        int config = 0;
        switch (dataBits) {
            case DATABITS_5:
            case DATABITS_6:
                throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
            case DATABITS_7:
            case DATABITS_8:
                config |= dataBits;
                break;
            default:
                throw new IllegalArgumentException("Invalid data bits: " + dataBits);
        }
        switch (parity) {
            case PARITY_NONE:
                break;
            case PARITY_ODD:
                config |= 0x100;
                break;
            case PARITY_EVEN:
                config |= 0x200;
                break;
            case PARITY_MARK:
                config |= 0x300;
                break;
            case PARITY_SPACE:
                config |= 0x400;
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
                config |= 0x1000;
                break;
            default:
                throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
        }
        int result = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, SET_DATA_REQUEST,
                config, mPortNumber + 1, null, 0, USB_TIMEOUT_MILLIS);
        if (result != 0) {
            throw new IOException("Setting parameters failed: result=" + result);
        }
        breakConfig = config;
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setBaudrate
     */
    private void setBaudrate(int baudRate) throws IOException {
        int divisor, subdivisor, effectiveBaudRate;
        if (baudRate > 3500000) {
            throw new UnsupportedOperationException("Baud rate to high");
        } else if (baudRate >= 2500000) {
            divisor = 0;
            subdivisor = 0;
            effectiveBaudRate = 3000000;
        } else if (baudRate >= 1750000) {
            divisor = 1;
            subdivisor = 0;
            effectiveBaudRate = 2000000;
        } else {
            divisor = (24000000 << 1) / baudRate;
            divisor = (divisor + 1) >> 1; // round
            subdivisor = divisor & 0x07;
            divisor >>= 3;
            if (divisor > 0x3fff) // exceeds bit 13 at 183 baud
                throw new UnsupportedOperationException("Baud rate to low");
            effectiveBaudRate = (24000000 << 1) / ((divisor << 3) + subdivisor);
            effectiveBaudRate = (effectiveBaudRate + 1) >> 1;
        }
        double baudRateError = Math.abs(1.0 - (effectiveBaudRate / (double) baudRate));
        if (baudRateError >= 0.031) // can happen only > 1.5Mbaud
            throw new UnsupportedOperationException(String.format("Baud rate deviation %.1f%% is higher than allowed 3%%", baudRateError * 100));
        int value = divisor;
        int index = 0;
        switch (subdivisor) {
            case 0:
                break; // 16,15,14 = 000 - sub-integer divisor = 0
            case 4:
                value |= 0x4000;
                break; // 16,15,14 = 001 - sub-integer divisor = 0.5
            case 2:
                value |= 0x8000;
                break; // 16,15,14 = 010 - sub-integer divisor = 0.25
            case 1:
                value |= 0xc000;
                break; // 16,15,14 = 011 - sub-integer divisor = 0.125
            case 3:
                value |= 0x0000;
                index |= 1;
                break; // 16,15,14 = 100 - sub-integer divisor = 0.375
            case 5:
                value |= 0x4000;
                index |= 1;
                break; // 16,15,14 = 101 - sub-integer divisor = 0.625
            case 6:
                value |= 0x8000;
                index |= 1;
                break; // 16,15,14 = 110 - sub-integer divisor = 0.75
            case 7:
                value |= 0xc000;
                index |= 1;
                break; // 16,15,14 = 111 - sub-integer divisor = 0.875
        }
        if (baudRateWithPort) {
            index <<= 8;
            index |= mPortNumber + 1;
        }
        Log.d(TAG, String.format("baud rate=%d, effective=%d, error=%.1f%%, value=0x%04x, index=0x%04x, divisor=%d, subdivisor=%d",
                baudRate, effectiveBaudRate, baudRateError * 100, value, index, divisor, subdivisor));
        int result = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, SET_BAUD_RATE_REQUEST,
                value, index, null, 0, USB_TIMEOUT_MILLIS);
        if (result != 0) {
            throw new IOException("Setting baudrate failed: result=" + result);
        }
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setBreak
     */
    public void setBreak(boolean value) throws IOException {
        int config = breakConfig;
        if (value) config |= 0x4000;
        int result = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, SET_DATA_REQUEST,
                config, mPortNumber + 1, null, 0, USB_TIMEOUT_MILLIS);
        if (result != 0) {
            throw new IOException("Setting BREAK failed: result=" + result);
        }
    }

    @Override
    public synchronized byte[] read(int timeout) throws IOException {
        long endTime = System.currentTimeMillis() + timeout;
        int nread = 0;
        byte[] bytes;
        do {
            bytes = super.read(timeout);
            if (bytes != null) {
                nread = bytes.length;
            }
        } while (nread == READ_HEADER_LENGTH && System.currentTimeMillis() < endTime);
        return bytes;
    }


}
