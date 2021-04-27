
package com.xc.framework.port.usb.driver;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import java.io.IOException;

/**
 * @Date：2021/4/25
 * @Author：ZhangXuanChen
 * @Description：Silabs CP210x, CP2105, CP2108, CP2102 and other CP210x single port devices
 */
public class UsbCp21xDriver extends UsbBaseDriver {
    private static final int REQTYPE_HOST_TO_DEVICE = 0x41;
    //
    private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0x00;
    private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 0x03;
    private static final int SILABSER_SET_BREAK_REQUEST_CODE = 0x05;
    private static final int SILABSER_SET_MHS_REQUEST_CODE = 0x07;
    private static final int SILABSER_SET_BAUDRATE = 0x1E;
    //
    private static final int UART_ENABLE = 0x0001;
    private static final int UART_DISABLE = 0x0000;
    //
    private static final int DTR_ENABLE = 0x101;
    private static final int DTR_DISABLE = 0x100;
    private static final int RTS_ENABLE = 0x202;
    private static final int RTS_DISABLE = 0x200;
    //
    private boolean mIsRestrictedPort;


    @Override
    protected void openInt() throws IOException {
        mIsRestrictedPort = mDevice.getInterfaceCount() == 2 && mPortNumber == 1;
        if (mPortNumber >= mDevice.getInterfaceCount()) {
            throw new IOException("Unknown port number");
        }
        UsbInterface dataIface = mDevice.getInterface(mPortNumber);
        if (!mConnection.claimInterface(dataIface, true)) {
            throw new IOException("Could not claim interface " + mPortNumber);
        }
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
        setConfigSingle(SILABSER_IFC_ENABLE_REQUEST_CODE, UART_ENABLE);
        setConfigSingle(SILABSER_SET_MHS_REQUEST_CODE, (dtr ? DTR_ENABLE : DTR_DISABLE) | (rts ? RTS_ENABLE : RTS_DISABLE));
        setParameters(baudRate, dataBits, stopBits, parity);
    }

    @Override
    protected void closeInt() {
        try {
            setConfigSingle(SILABSER_IFC_ENABLE_REQUEST_CODE, UART_DISABLE);
        } catch (Exception ignored) {
        }
        try {
            mConnection.releaseInterface(mDevice.getInterface(mPortNumber));
        } catch (Exception ignored) {
        }
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setConfigSingle
     */
    private void setConfigSingle(int request, int value) throws IOException {
        int result = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, request, value,
                mPortNumber, null, 0, USB_TIMEOUT_MILLIS);
        if (result != 0) {
            throw new IOException("Control transfer failed: " + request + " / " + value + " -> " + result);
        }
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setParameters
     */
    private void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        if (baudRate <= 0) {
            throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
        }
        setBaudRate(baudRate);
        int configDataBits = 0;
        switch (dataBits) {
            case DATABITS_5:
                if (mIsRestrictedPort)
                    throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
                configDataBits |= 0x0500;
                break;
            case DATABITS_6:
                if (mIsRestrictedPort)
                    throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
                configDataBits |= 0x0600;
                break;
            case DATABITS_7:
                if (mIsRestrictedPort)
                    throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
                configDataBits |= 0x0700;
                break;
            case DATABITS_8:
                configDataBits |= 0x0800;
                break;
            default:
                throw new IllegalArgumentException("Invalid data bits: " + dataBits);
        }
        switch (parity) {
            case PARITY_NONE:
                break;
            case PARITY_ODD:
                configDataBits |= 0x0010;
                break;
            case PARITY_EVEN:
                configDataBits |= 0x0020;
                break;
            case PARITY_MARK:
                if (mIsRestrictedPort)
                    throw new UnsupportedOperationException("Unsupported parity: mark");
                configDataBits |= 0x0030;
                break;
            case PARITY_SPACE:
                if (mIsRestrictedPort)
                    throw new UnsupportedOperationException("Unsupported parity: space");
                configDataBits |= 0x0040;
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
                if (mIsRestrictedPort)
                    throw new UnsupportedOperationException("Unsupported stop bits: 2");
                configDataBits |= 2;
                break;
            default:
                throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
        }
        setConfigSingle(SILABSER_SET_LINE_CTL_REQUEST_CODE, configDataBits);
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setBaudRate
     */
    private void setBaudRate(int baudRate) throws IOException {
        byte[] data = new byte[]{
                (byte) (baudRate & 0xff),
                (byte) ((baudRate >> 8) & 0xff),
                (byte) ((baudRate >> 16) & 0xff),
                (byte) ((baudRate >> 24) & 0xff)
        };
        int ret = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, SILABSER_SET_BAUDRATE,
                0, mPortNumber, data, 4, USB_TIMEOUT_MILLIS);
        if (ret < 0) {
            throw new IOException("Error setting baud rate");
        }
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setBreak
     */
    public void setBreak(boolean value) throws IOException {
        setConfigSingle(SILABSER_SET_BREAK_REQUEST_CODE, value ? 1 : 0);
    }
}
