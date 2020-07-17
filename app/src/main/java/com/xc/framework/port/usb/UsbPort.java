package com.xc.framework.port.usb;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.xc.framework.port.core.IPort;
import com.xc.framework.port.core.PortParam;

import java.io.IOException;
import java.util.Arrays;

/**
 * Date：2019/12/3
 * Author：ZhangXuanChen
 * Description：UsbPort
 */
public class UsbPort implements IPort {
    private static final String ACTION_USB_PERMISSION = "com.xc.framework.USB_PERMISSION";
    /**
     * timeout
     */
    private static final int READ_AND_WRITE_MAX_SIZE = 16 * 1024;//16384
    /**
     * Configuration Request Types
     */
    private static final int REQTYPE_HOST_TO_DEVICE = 0x41;
    /**
     * Configuration Request Codes
     */
    private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0x00;
    private static final int SILABSER_SET_BAUDDIV_REQUEST_CODE = 0x01;
    private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 0x03;
    private static final int SILABSER_SET_MHS_REQUEST_CODE = 0x07;
    private static final int SILABSER_SET_BAUDRATE = 0x1E;
    /**
     * SILABSER_IFC_ENABLE_REQUEST_CODE
     */
    private static final int UART_ENABLE = 0x0001;
    /**
     * SILABSER_SET_BAUDDIV_REQUEST_CODE
     */
    private static final int BAUD_RATE_GEN_FREQ = 0x384000;
    /**
     * SILABSER_SET_MHS_REQUEST_CODE
     */
    private static final int MCR_ALL = 0x0003;
    private static final int CONTROL_WRITE_DTR = 0x0100;
    private static final int CONTROL_WRITE_RTS = 0x0200;
    /**
     * 5 data bits.
     */
    private static final int DATABITS_5 = 5;
    /**
     * 6 data bits.
     */
    private static final int DATABITS_6 = 6;
    /**
     * 7 data bits.
     */
    private static final int DATABITS_7 = 7;
    /**
     * 8 data bits.
     */
    private static final int DATABITS_8 = 8;
    /**
     * No parity.
     */
    private static final int PARITY_NONE = 0;
    /**
     * Odd parity.
     */
    private static final int PARITY_ODD = 1;
    /**
     * Even parity.
     */
    private static final int PARITY_EVEN = 2;
    /**
     * Mark parity.
     */
    private static final int PARITY_MARK = 3;
    /**
     * Space parity.
     */
    private static final int PARITY_SPACE = 4;
    /**
     * 1 stop bit.
     */
    private static final int STOPBITS_1 = 1;
    /**
     * 1.5 stop bits.
     */
    private static final int STOPBITS_1_5 = 3;
    /**
     * 2 stop bits.
     */
    private static final int STOPBITS_2 = 2;
    //
    private Context mContext;
    private UsbManager mUsbManager;
    //
    private UsbDeviceConnection mUc;
    private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:53
     * Description：UsbPort
     */
    public UsbPort(Context context) {
        this.mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:08
     * Description：getReadEndpoint
     */
    public UsbEndpoint getReadEndpoint() {
        return mReadEndpoint;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:08
     * Description：getWriteEndpoint
     */
    public UsbEndpoint getWriteEndpoint() {
        return mWriteEndpoint;
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:56
     * Description：打开串口
     *
     * @param portParam usb参数
     */
    @Override
    public boolean openPort(PortParam portParam) {
        UsbPortParam usbPortParam = (UsbPortParam) portParam;
        if (usbPortParam == null) {
            return false;
        }
        return openPort(
                usbPortParam.getUsbDevice(),//usb设备
                usbPortParam.getBaudrate(),//波特率
                usbPortParam.getDataBits(),//数据位，默认8
                usbPortParam.getStopBits(),//停止位，默认1
                usbPortParam.getParity());//奇偶校验位，默认0（无校验）
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:56
     * Description：打开串口
     *
     * @param usbDevice usb设备
     * @param baudrate  波特率
     * @param dataBits  数据位，默认8
     * @param stopBits  停止位，默认1
     * @param parity    奇偶校验位，默认0（无校验）
     */
    public boolean openPort(UsbDevice usbDevice, int baudrate, int dataBits, int stopBits, int parity) {
        if (!isPermission(mUsbManager, usbDevice)) {
            getUsbPermission(usbDevice);
            return false;
        }
        try {
            mUc = mUsbManager.openDevice(usbDevice);
            if (mUc == null) {
                return false;
            }
            if (usbDevice.getInterfaceCount() <= 0) {
                return false;
            }
            UsbInterface usbInterface = usbDevice.getInterface(0);
            if (usbInterface == null) {
                return false;
            }
            if (!mUc.claimInterface(usbInterface, true)) {
                return false;
            }
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint ep = usbInterface.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                        mReadEndpoint = ep;
                    } else if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        mWriteEndpoint = ep;
                    }
                }
            }
            if (mReadEndpoint == null || mWriteEndpoint == null) {
                return false;
            }
            //setConfigSingle
            setConfigSingle(mUc, SILABSER_IFC_ENABLE_REQUEST_CODE, UART_ENABLE);
            setConfigSingle(mUc, SILABSER_SET_MHS_REQUEST_CODE, MCR_ALL | CONTROL_WRITE_DTR | CONTROL_WRITE_RTS);
            setConfigSingle(mUc, SILABSER_SET_BAUDDIV_REQUEST_CODE, BAUD_RATE_GEN_FREQ / baudrate);
            //setParameters
            setParameters(mUc, baudrate, dataBits, stopBits, parity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:14
     * Description：关闭串口
     */
    @Override
    public boolean closePort() {
        try {
            if (mReadEndpoint != null) {
                mReadEndpoint = null;
            }
            if (mWriteEndpoint != null) {
                mWriteEndpoint = null;
            }
            if (mUc != null) {
                mUc.close();
                mUc = null;
            }
            if (mUsbManager != null) {
                mUsbManager = null;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 14:40
     * Description：readUsbPort
     * Param：buffer readUsbPort
     * Return：int
     */
    @Override
    public synchronized byte[] readPort() {
        byte[] bytes = null;
        try {
            if (mReadEndpoint != null && mUc != null) {
                byte[] bufferDatas = new byte[1024];
                int readSize = mUc.bulkTransfer(mReadEndpoint, bufferDatas, getMaxPacketSize(), 1);
                if (readSize > 0) {
                    bytes = Arrays.copyOf(bufferDatas, readSize);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 14:40
     * Description：writeUsbPort
     * Param：buffer writeUsbPort
     * Return：boolean
     */
    @Override
    public synchronized boolean writePort(byte[] bytes) {
        try {
            if (mWriteEndpoint != null && mUc != null && bytes != null && bytes.length > 0) {
                int writeSize = mUc.bulkTransfer(mWriteEndpoint, bytes, getMaxPacketSize(), 1);
                return writeSize > 0 ? true : false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:49
     * Description：检查权限
     */
    private boolean isPermission(UsbManager mUsbManager, UsbDevice mUsbDevice) {
        if (mUsbManager == null || mUsbDevice == null) {
            return false;
        }
        if (!mUsbManager.hasPermission(mUsbDevice)) {
            return false;
        }
        return true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 9:36
     * Description：获取usb权限
     */
    private void getUsbPermission(UsbDevice usbDevice) {
        if (mContext == null || mUsbManager == null) {
            return;
        }
        try {
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            mContext.registerReceiver(usbPermissionReceiver, filter);
            mUsbManager.requestPermission(usbDevice, pi);
        } catch (Exception e) {
            return;
        }
        return;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 9:30
     * Description：usb权限广播
     */
    private BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                context.unregisterReceiver(this);//解注册
//                synchronized (this) {
//                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUsbDevice.equals(usbDevice)) {
//                        Log.i(TAG, "已获取USB权限：" + usbDevice.getDeviceName());
//                    } else {
//                        Log.i(TAG, "USB权限已被拒绝：" + usbDevice.getDeviceName());
//                    }
//                }
            }
        }
    };

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:47
     * Description：setParameters
     */
    public void setParameters(UsbDeviceConnection connection, int baudrate, int dataBits, int stopBits, int parity) throws IOException {
        if (baudrate <= 0) {
            throw new IllegalArgumentException("Invalid baud rate: " + baudrate);
        }
        //baudrate
        setBaudRate(connection, baudrate);
        int configDataBits = 0;
        //dataBits
        switch (dataBits) {
            case DATABITS_5:
                configDataBits |= 0x0500;
                break;
            case DATABITS_6:
                configDataBits |= 0x0600;
                break;
            case DATABITS_7:
                configDataBits |= 0x0700;
                break;
            case DATABITS_8:
                configDataBits |= 0x0800;
                break;
            default:
                throw new IllegalArgumentException("Invalid data bits: " + dataBits);
        }
        //parity
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
                configDataBits |= 0x0030;
                break;
            case PARITY_SPACE:
                configDataBits |= 0x0040;
                break;
            default:
                throw new IllegalArgumentException("Invalid parity: " + parity);
        }
        //stopBits
        switch (stopBits) {
            case STOPBITS_1:
                break;
            case STOPBITS_1_5:
                throw new UnsupportedOperationException("Unsupported stop bits: 1.5");
            case STOPBITS_2:
                configDataBits |= 2;
                break;
            default:
                throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
        }
        setConfigSingle(connection, SILABSER_SET_LINE_CTL_REQUEST_CODE, configDataBits);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:44
     * Description：setBaudRate
     */
    private void setBaudRate(UsbDeviceConnection connection, int baudRate) throws IOException {
        byte[] data = new byte[]{
                (byte) (baudRate & 0xff),
                (byte) ((baudRate >> 8) & 0xff),
                (byte) ((baudRate >> 16) & 0xff),
                (byte) ((baudRate >> 24) & 0xff)
        };
        int ret = connection.controlTransfer(REQTYPE_HOST_TO_DEVICE, SILABSER_SET_BAUDRATE, 0, 0, data, 4, 1);
        if (ret < 0) {
            throw new IOException("Error setting baud rate");
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:39
     * Description：
     * Param：null
     * Return：
     */
    private int setConfigSingle(UsbDeviceConnection connection, int request, int value) throws IOException {
        int result = connection.controlTransfer(REQTYPE_HOST_TO_DEVICE, request, value, 0, null, 0, 1);
        if (result != 0) {
            throw new IOException("Setting baudrate failed: result=" + result);
        }
        return result;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/5 10:05
     * Description：获取最大读写包大小
     */
    public int getMaxPacketSize() {
        return READ_AND_WRITE_MAX_SIZE;
    }


}
