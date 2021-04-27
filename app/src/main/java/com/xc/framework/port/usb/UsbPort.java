package com.xc.framework.port.usb;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.xc.framework.port.core.IPort;
import com.xc.framework.port.core.PortParam;
import com.xc.framework.port.usb.driver.UsbBaseDriver;
import com.xc.framework.port.usb.driver.UsbCdcAcmDriver;
import com.xc.framework.port.usb.driver.UsbCh34xDriver;
import com.xc.framework.port.usb.driver.UsbCp21xDriver;
import com.xc.framework.port.usb.driver.UsbFtdiDriver;
import com.xc.framework.port.usb.driver.UsbProlificDriver;
import com.xc.framework.port.usb.driver.UsbType;

/**
 * Date：2019/12/3
 * Author：ZhangXuanChen
 * Description：UsbPort
 */
public class UsbPort implements IPort {
    private static final String ACTION_USB_PERMISSION = "com.xc.framework.USB_PERMISSION";
    private Context mContext;
    private UsbManager mUsbManager;
    //
    private UsbBaseDriver mDriver;
    private UsbDeviceConnection mUc;
    private int sendTimeout;//发送超时，默认1000

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 8:53
     * Description：UsbPort
     */
    public UsbPort(Context context, UsbType usbType) {
        this.mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbType == UsbType.Ch34x) {
            mDriver = new UsbCh34xDriver();
        } else if (usbType == UsbType.Ftdi) {
            mDriver = new UsbFtdiDriver();
        } else if (usbType == UsbType.Cp21x) {
            mDriver = new UsbCp21xDriver();
        } else if (usbType == usbType.Prolific) {
            mDriver = new UsbProlificDriver();
        } else if (usbType == UsbType.CdcAcm) {
            mDriver = new UsbCdcAcmDriver();
        }
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
                usbPortParam.getBaudRate(),//波特率
                usbPortParam.getDataBits(),//数据位，默认8
                usbPortParam.getStopBits(),//停止位，默认1
                usbPortParam.getParity(),//奇偶校验位，默认0（无校验）
                usbPortParam.getSendTimeout());//发送超时
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 10:56
     * Description：打开串口
     *
     * @param usbDevice   usb设备
     * @param baudRate    波特率
     * @param dataBits    数据位，默认8
     * @param stopBits    停止位，默认1
     * @param parity      奇偶校验位，默认0（无校验）
     * @param sendTimeout 发送超时，默认1000
     */
    public boolean openPort(UsbDevice usbDevice, int baudRate, int dataBits, int stopBits, int parity, int sendTimeout) {
        this.sendTimeout = sendTimeout;
        try {
            if (!isPermission(mUsbManager, usbDevice)) {
                getUsbPermission(usbDevice);
                return false;
            }
            mUc = mUsbManager.openDevice(usbDevice);
            if (mUc == null) {
                return false;
            }
            if (usbDevice.getInterfaceCount() <= 0) {
                return false;
            }
            if (mDriver == null) {
                return false;
            }
            mDriver.open(usbDevice, mUc, baudRate, dataBits, stopBits, parity);
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
            if (mDriver != null) {
                mDriver.close();
                mDriver = null;
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
     * Description：readPort
     */
    @Override
    public synchronized byte[] readPort() {
        byte[] bytes = null;
        try {
            if (mDriver != null) {
                mDriver.read(sendTimeout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 14:40
     * Description：writePort
     */
    @Override
    public synchronized boolean writePort(byte[] bytes) {
        try {
            if (mDriver != null) {
                return mDriver.write(bytes, sendTimeout);
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
        if (mContext == null || mUsbManager == null || usbDevice == null) {
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

}
