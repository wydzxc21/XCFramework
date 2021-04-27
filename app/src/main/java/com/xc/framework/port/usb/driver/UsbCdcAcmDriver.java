package com.xc.framework.port.usb.driver;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import java.io.IOException;

/**
 * @Date：2021/4/27
 * @Author：ZhangXuanChen
 * @Description：CDC driver, Arduino, Teensyduino, Atmel Lufa,Leaflabs Maple,ARM mbed
 */
public class UsbCdcAcmDriver extends UsbBaseDriver {
    public static final String TAG = "UsbCdcAcmDriver";
    private static final int SET_LINE_CODING = 0x20;  // USB CDC 1.1 section 6.2
    private static final int USB_RECIP_INTERFACE = 0x01;
    private static final int USB_RT_ACM = UsbConstants.USB_TYPE_CLASS | USB_RECIP_INTERFACE;
    private static final int SEND_BREAK = 0x23;
    //
    private UsbInterface mControlInterface;
    private UsbInterface mDataInterface;
    private int mControlIndex;
    private UsbEndpoint mControlEndpoint;

    @Override
    protected void openInt() throws IOException {
        if (mPortNumber == -1) {
            Log.d(TAG, "device might be castrated ACM device, trying single interface logic");
            openSingleInterface();
        } else {
            Log.d(TAG, "trying default interface logic");
            openInterface();
        }
        setParameters(baudRate, dataBits, stopBits, parity);
    }

    @Override
    protected void closeInt() {
        try {
            mConnection.releaseInterface(mControlInterface);
            mConnection.releaseInterface(mDataInterface);
        } catch (Exception ignored) {
        }
    }

    /**
     * @Date：2021/4/27
     * @Author：ZhangXuanChen
     * @Description：openSingleInterface
     */
    private void openSingleInterface() throws IOException {
        // the following code is inspired by the cdc-acm driver in the linux kernel
        mControlIndex = 0;
        mControlInterface = mDevice.getInterface(0);
        mDataInterface = mDevice.getInterface(0);
        if (!mConnection.claimInterface(mControlInterface, true)) {
            throw new IOException("Could not claim shared control/data interface");
        }
        for (int i = 0; i < mControlInterface.getEndpointCount(); ++i) {
            UsbEndpoint ep = mControlInterface.getEndpoint(i);
            if ((ep.getDirection() == UsbConstants.USB_DIR_IN) && (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT)) {
                mControlEndpoint = ep;
            } else if ((ep.getDirection() == UsbConstants.USB_DIR_IN) && (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)) {
                mReadEndpoint = ep;
            } else if ((ep.getDirection() == UsbConstants.USB_DIR_OUT) && (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)) {
                mWriteEndpoint = ep;
            }
        }
        if (mControlEndpoint == null) {
            throw new IOException("No control endpoint");
        }
    }

    /**
     * @Date：2021/4/27
     * @Author：ZhangXuanChen
     * @Description：
     */
    private void openInterface() throws IOException {
        Log.d(TAG, "claiming interfaces, count=" + mDevice.getInterfaceCount());
        int controlInterfaceCount = 0;
        int dataInterfaceCount = 0;
        mControlInterface = null;
        mDataInterface = null;
        for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
            UsbInterface usbInterface = mDevice.getInterface(i);
            if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_COMM) {
                if (controlInterfaceCount == mPortNumber) {
                    mControlIndex = i;
                    mControlInterface = usbInterface;
                }
                controlInterfaceCount++;
            }
            if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA) {
                if (dataInterfaceCount == mPortNumber) {
                    mDataInterface = usbInterface;
                }
                dataInterfaceCount++;
            }
        }
        if (mControlInterface == null) {
            throw new IOException("No control interface");
        }
        Log.d(TAG, "Control iface=" + mControlInterface);
        if (!mConnection.claimInterface(mControlInterface, true)) {
            throw new IOException("Could not claim control interface");
        }
        mControlEndpoint = mControlInterface.getEndpoint(0);
        if (mControlEndpoint.getDirection() != UsbConstants.USB_DIR_IN || mControlEndpoint.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {
            throw new IOException("Invalid control endpoint");
        }
        if (mDataInterface == null) {
            throw new IOException("No data interface");
        }
        Log.d(TAG, "data iface=" + mDataInterface);
        if (!mConnection.claimInterface(mDataInterface, true)) {
            throw new IOException("Could not claim data interface");
        }
        for (int i = 0; i < mDataInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = mDataInterface.getEndpoint(i);
            if (ep.getDirection() == UsbConstants.USB_DIR_IN && ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
                mReadEndpoint = ep;
            if (ep.getDirection() == UsbConstants.USB_DIR_OUT && ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
                mWriteEndpoint = ep;
        }
    }

    /**
     * @Date：2021/4/27
     * @Author：ZhangXuanChen
     * @Description：setParameters
     */
    private void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        if (baudRate <= 0) {
            throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
        }
        if (dataBits < DATABITS_5 || dataBits > DATABITS_8) {
            throw new IllegalArgumentException("Invalid data bits: " + dataBits);
        }
        byte stopBitsByte;
        switch (stopBits) {
            case STOPBITS_1:
                stopBitsByte = 0;
                break;
            case STOPBITS_1_5:
                stopBitsByte = 1;
                break;
            case STOPBITS_2:
                stopBitsByte = 2;
                break;
            default:
                throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
        }

        byte parityBitesByte;
        switch (parity) {
            case PARITY_NONE:
                parityBitesByte = 0;
                break;
            case PARITY_ODD:
                parityBitesByte = 1;
                break;
            case PARITY_EVEN:
                parityBitesByte = 2;
                break;
            case PARITY_MARK:
                parityBitesByte = 3;
                break;
            case PARITY_SPACE:
                parityBitesByte = 4;
                break;
            default:
                throw new IllegalArgumentException("Invalid parity: " + parity);
        }
        byte[] msg = {
                (byte) (baudRate & 0xff),
                (byte) ((baudRate >> 8) & 0xff),
                (byte) ((baudRate >> 16) & 0xff),
                (byte) ((baudRate >> 24) & 0xff),
                stopBitsByte,
                parityBitesByte,
                (byte) dataBits};
        sendAcmControlMessage(SET_LINE_CODING, 0, msg);
    }

    /**
     * @Date：2021/4/27
     * @Author：ZhangXuanChen
     * @Description：sendAcmControlMessage
     */
    private int sendAcmControlMessage(int request, int value, byte[] buf) throws IOException {
        int len = mConnection.controlTransfer(
                USB_RT_ACM, request, value, mControlIndex, buf, buf != null ? buf.length : 0, USB_TIMEOUT_MILLIS);
        if (len < 0) {
            throw new IOException("controlTransfer failed");
        }
        return len;
    }

    /**
     * @Date：2021/4/27
     * @Author：ZhangXuanChen
     * @Description：setBreak
     */
    public void setBreak(boolean value) throws IOException {
        sendAcmControlMessage(SEND_BREAK, value ? 0xffff : 0, null);
    }
}
