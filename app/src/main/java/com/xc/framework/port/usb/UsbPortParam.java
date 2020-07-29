package com.xc.framework.port.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.xc.framework.port.core.PortParamCallback;
import com.xc.framework.port.core.PortParam;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：usb参数
 */
public class UsbPortParam extends PortParam {
    /**
     * usb设备
     */
    private UsbDevice usbDevice;

    public UsbPortParam(UsbDevice usbDevice, int baudrate) {
        this.usbDevice = usbDevice;
        this.baudrate = baudrate;
    }

    public UsbPortParam(Context context, int vid, int pid, int baudrate) {
        this.usbDevice = UsbPortFinder.getInstance().getUsbDevice(context, vid, pid);
        this.baudrate = baudrate;
    }

    public UsbPortParam(Context context, int vid, int pid, int baudrate, int dataBits, int stopBits, int parity, int resendCount, int sendTimeout, int interruptTimeout, byte[] receiveResponseFrameHeads, byte[] receiveRequestFrameHeads, PortParamCallback portParamCallback) {
        this.usbDevice = UsbPortFinder.getInstance().getUsbDevice(context, vid, pid);
        this.baudrate = baudrate;
        this.dataBits = dataBits >= 0 ? dataBits : this.dataBits;
        this.stopBits = stopBits >= 0 ? stopBits : this.stopBits;
        this.parity = parity >= 0 ? parity : this.parity;
        this.resendCount = resendCount > 0 ? resendCount : this.resendCount;
        this.sendTimeout = sendTimeout > 0 ? sendTimeout : this.sendTimeout;
        this.interruptTimeout = interruptTimeout > 0 ? interruptTimeout : this.interruptTimeout;
        this.receiveResponseFrameHeads = receiveResponseFrameHeads;
        this.receiveRequestFrameHeads = receiveRequestFrameHeads;
        this.portParamCallback = portParamCallback;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public void setUsbDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    public void setUsbDevice(Context context, int vid, int pid) {
        this.usbDevice = UsbPortFinder.getInstance().getUsbDevice(context, vid, pid);
    }

}
