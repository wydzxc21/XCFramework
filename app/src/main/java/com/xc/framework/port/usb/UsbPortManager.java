package com.xc.framework.port.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.xc.framework.port.core.IPort;
import com.xc.framework.port.core.PortManager;
import com.xc.framework.port.core.PortParam;
import com.xc.framework.port.core.PortParamCallback;
import com.xc.framework.port.usb.driver.UsbType;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：usb管理类
 */
public class UsbPortManager extends PortManager {
    private Context mContext;
    private UsbPort mUsbPort;
    private UsbPortParam mUsbPortParam;

    public UsbPortManager(Context context, UsbType usbType) {
        mContext = context;
        mUsbPort = new UsbPort(mContext, usbType);
    }

    @Override
    public IPort getIPort() {
        return mUsbPort;
    }

    @Override
    public PortParam getPortParam() {
        return mUsbPortParam;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：SerialPortParam usb参数
     */
    public void init(UsbPortParam usbPortParam) {
        this.mUsbPortParam = usbPortParam;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化usb
     * Param：UsbDevice usb设备
     * Param：baudRate 波特率
     */
    public void init(UsbDevice usbDevice, int baudRate) {
        this.mUsbPortParam = new UsbPortParam(usbDevice, baudRate);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 13:41
     * Description：初始化usb
     * Param：context 上下文
     * Param：vid 厂商id
     * Param：pid 设备id
     * Param：baudRate 波特率
     */
    public void init(int vid, int pid, int baudRate) {
        this.mUsbPortParam = new UsbPortParam(mContext, vid, pid, baudRate);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化usb
     * Param：context 上下文
     * Param：vid 厂商id
     * Param：pid 设备id
     * Param：baudRate 波特率
     * Param：resendCount 重发次数，默认0
     * Param：sendTimeout 发送超时(毫秒)，默认1000
     * Param：interruptTimeout 中断超时(毫秒)，默认10*1000
     * Param：receiveResponseFrameHeads 接收响应帧头，默认null
     * Param：receiveRequestFrameHeads 接收请求帧头，默认null
     * Param：portParamCallback 设置串口参数回调，默认null
     */
    public void init(int vid, int pid, int baudRate, int resendCount, int sendTimeout, int interruptTimeout, byte[] receiveResponseFrameHeads, byte[] receiveRequestFrameHeads, PortParamCallback portParamCallback) {
        this.mUsbPortParam = new UsbPortParam(mContext, vid, pid, baudRate);
        this.mUsbPortParam.setResendCount(resendCount);
        this.mUsbPortParam.setSendTimeout(sendTimeout);
        this.mUsbPortParam.setInterruptTimeout(interruptTimeout);
        this.mUsbPortParam.setReceiveResponseFrameHeads(receiveResponseFrameHeads);
        this.mUsbPortParam.setReceiveRequestFrameHeads(receiveRequestFrameHeads);
        this.mUsbPortParam.setPortParamCallback(portParamCallback);
    }

}
