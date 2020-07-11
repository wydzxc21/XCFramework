package com.xc.framework.port.usb;


import com.xc.framework.port.core.PortSendCallable;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：usb发送
 */
public abstract class UsbPortSendCallable extends PortSendCallable {
    private final String TAG = "UsbPortSendRunnable";
    private UsbPort usbPort;

    /**
     * @param sendDatas            发送数据
     * @param what                 区分消息
     * @param isWaitResponse       是否等待响应
     * @param usbPortParam         串口参数
     * @param usbPort              usb工具
     * @param usbPortReceiveThread 接收线程
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public UsbPortSendCallable(byte[] sendDatas, int what, boolean isWaitResponse, UsbPortParam usbPortParam, UsbPort usbPort, UsbPortReceiveThread usbPortReceiveThread) {
        super(sendDatas, what, isWaitResponse, usbPortParam, usbPortReceiveThread);
        this.usbPort = usbPort;
    }

    @Override
    protected boolean writePort(byte[] sendDatas) {
        return usbPort.writeUsbPort(sendDatas);
    }
}
