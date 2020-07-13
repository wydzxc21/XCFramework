package com.xc.framework.port.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.xc.framework.port.core.IPort;
import com.xc.framework.port.core.LengthCallback;
import com.xc.framework.port.core.PortManager;
import com.xc.framework.port.core.PortParam;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：usb管理类
 */
public class UsbPortManager extends PortManager {
    private Context mContext;
    private UsbPortParam mUsbPortParam;

    public UsbPortManager(Context context) {
        mContext = context;
    }

    @Override
    public IPort getIPort() {
        return UsbPort.getInstance(mContext);
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
     * Param：baudrate 波特率
     */
    public void init(UsbDevice usbDevice, int baudrate) {
        this.mUsbPortParam = new UsbPortParam(usbDevice, baudrate);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 13:41
     * Description：初始化usb
     * Param：context 上下文
     * Param：vid 厂商id
     * Param：pid 设备id
     * Param：baudrate 波特率
     */
    public void init(int vid, int pid, int baudrate) {
        this.mUsbPortParam = new UsbPortParam(mContext, vid, pid, baudrate);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化usb
     * Param：context 上下文
     * Param：vid 厂商id
     * Param：pid 设备id
     * Param：baudrate 波特率
     * Param：resendCount 重发次数，默认0
     * Param：sendTimeout 发送超时(毫秒)，默认2000
     * Param：receiveResponseFrameHeads 接收响应帧头，默认null
     * Param：receiveRequestFrameHeads 接收请求帧头，默认null
     * Param：lengthCallback 设置长度回调，默认null
     */
    public void init(int vid, int pid, int baudrate, int resendCount, int sendTimeout, byte[] receiveResponseFrameHeads, byte[] receiveRequestFrameHeads, LengthCallback lengthCallback) {
        this.mUsbPortParam = new UsbPortParam(mContext, vid, pid, baudrate);
        this.mUsbPortParam.setResendCount(resendCount);
        this.mUsbPortParam.setSendTimeout(sendTimeout);
        this.mUsbPortParam.setReceiveResponseFrameHeads(receiveResponseFrameHeads);
        this.mUsbPortParam.setReceiveRequestFrameHeads(receiveRequestFrameHeads);
        this.mUsbPortParam.setLengthCallback(lengthCallback);
    }

}
