package com.xc.framework.port.usb.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Date：2021/4/25
 * @Author：ZhangXuanChen
 * @Description：usb驱动基类
 */
public abstract class UsbBaseDriver {
    protected final int USB_TIMEOUT_MILLIS = 1000;
    //
    protected final int DATABITS_5 = 5;//5位数据位
    protected final int DATABITS_6 = 6;//6位数据位
    protected final int DATABITS_7 = 7;//7位数据位
    protected final int DATABITS_8 = 8;//8位数据位
    protected final int PARITY_NONE = 0;//无奇偶校验
    protected final int PARITY_ODD = 1;//奇校验
    protected final int PARITY_EVEN = 2;//偶校验
    protected final int PARITY_MARK = 3;//校验位始终为1
    protected final int PARITY_SPACE = 4;//校验位始终为0
    protected final int STOPBITS_1 = 1;//1位停止位
    protected final int STOPBITS_1_5 = 3;//1.5位停止位
    protected final int STOPBITS_2 = 2;//2位停止位
    //
    protected UsbDevice mDevice;
    protected UsbDeviceConnection mConnection;
    protected UsbEndpoint mReadEndpoint;
    protected UsbEndpoint mWriteEndpoint;
    //
    protected boolean dtr = false;
    protected boolean rts = false;
    protected int mPortNumber = 0;
    protected int baudRate, dataBits, stopBits, parity;

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：getDevice
     */
    protected UsbDevice getDevice() {
        return mDevice;
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：getPortNumber
     */
    public int getPortNumber() {
        return mPortNumber;
    }

    /**
     * @Date：2021/4/26
     * @Author：ZhangXuanChen
     * @Description：setmPortNumber
     */
    public void setPortNumber(int portNumber) {
        this.mPortNumber = portNumber;
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：getSerial
     */
    protected String getSerial() {
        return mConnection.getSerial();
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：getWriteEndpoint
     */
    protected UsbEndpoint getWriteEndpoint() {
        return mWriteEndpoint;
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：getReadEndpoint
     */
    protected UsbEndpoint getReadEndpoint() {
        return mReadEndpoint;
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：openInt
     */
    protected abstract void openInt() throws IOException;

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：closeInt
     */
    protected abstract void closeInt();

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：open
     */
    public synchronized void open(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection, int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        if (mConnection != null) {
            throw new IOException("Already open");
        }
        if (usbDeviceConnection == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        this.mDevice = usbDevice;
        this.mConnection = usbDeviceConnection;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        try {
            openInt();
            if (mReadEndpoint == null || mWriteEndpoint == null) {
                throw new IOException("Could not get read & write endpoints");
            }
        } catch (Exception e) {
            try {
                close();
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：close
     */
    public synchronized void close() throws IOException {
        if (mConnection == null) {
            throw new IOException("Already closed");
        }
        try {
            closeInt();
        } catch (Exception ignored) {
        }
        try {
            mConnection.close();
        } catch (Exception ignored) {
        }
        mConnection = null;
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：use simple USB request supported by all devices to test if connection is still valid
     */
    public synchronized void testConnection() throws IOException {
        byte[] buf = new byte[2];
        int len = mConnection.controlTransfer(0x80 /*DEVICE*/, 0 /*GET_STATUS*/, 0, 0, buf, buf.length, 200);
        if (len < 0)
            throw new IOException("USB get_status request failed");
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：read
     */
    public synchronized byte[] read(int timeout) throws IOException {
        if (mConnection == null) {
            throw new IOException("Connection closed");
        }
        if (timeout <= 0) {
            timeout = USB_TIMEOUT_MILLIS;
        }
        byte[] bytes = new byte[1024];
        int len = mConnection.bulkTransfer(mReadEndpoint, bytes, bytes.length, timeout);
        if (len > 0) {
            return Arrays.copyOf(bytes, len);
        }
        return null;
    }

    /**
     * @Date：2021/4/25
     * @Author：ZhangXuanChen
     * @Description：write
     */
    public synchronized boolean write(byte[] bytes, int timeout) throws IOException {
        if (mConnection == null) {
            throw new IOException("Connection closed");
        }
        if (bytes.length <= 0) {
            throw new IllegalArgumentException("Read buffer to small");
        }
        if (timeout <= 0) {
            timeout = USB_TIMEOUT_MILLIS;
        }
        int len = mConnection.bulkTransfer(mWriteEndpoint, bytes, bytes.length, timeout);
        return len > 0 ? true : false;
    }
}
