package com.xc.framework.port.usb;

import com.xc.framework.port.core.PortReceiveThread;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：usb接收
 */
public abstract class UsbPortReceiveThread extends PortReceiveThread {
    private final String TAG = "UsbPortReceiveThread";
    private UsbPort usbPort;

    /**
     * @param usbPortParam 串口参数
     * @param usbPort         usb工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public UsbPortReceiveThread(UsbPortParam usbPortParam, UsbPort usbPort) {
        super(usbPortParam);
        this.usbPort = usbPort;
    }

    @Override
    protected byte[] readPort() {
        return usbPort.readUsbPort();
    }
}
