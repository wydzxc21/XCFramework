package com.xc.framework.port.serial;

import com.xc.framework.port.core.IPort;
import com.xc.framework.port.core.PortManager;
import com.xc.framework.port.core.PortParamCallback;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：串口管理类
 */
public class SerialPortManager extends PortManager {
    private SerialPort mSerialPort;
    private SerialPortParam mSerialPortParam;

    public SerialPortManager() {
        mSerialPort = new SerialPort();
    }

    @Override
    public IPort getIPort() {
        return mSerialPort;
    }

    @Override
    public SerialPortParam getPortParam() {
        return mSerialPortParam;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：SerialPortParam 串口参数
     */
    public void init(SerialPortParam serialPortParam) {
        this.mSerialPortParam = serialPortParam;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：devicePath 串口地址
     * Param：baudRate 波特率
     */
    public void init(String devicePath, int baudRate) {
        this.mSerialPortParam = new SerialPortParam(devicePath, baudRate);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：devicePath 串口地址
     * Param：baudRate 波特率
     * Param：resendCount 重发次数，默认0，不重发
     * Param：sendTimeout 发送超时(毫秒)，默认2000
     * Param：runTimeout 运行超时(毫秒)，默认5000
     * Param：receiveResponseFrameHeads 接收响应帧头，默认null
     * Param：receiveRequestFrameHeads 接收请求帧头，默认null
     * Param：portParamCallback 设置串口参数回调，默认null
     */
    public void init(String devicePath, int baudRate, int resendCount, int sendTimeout, int runTimeout, byte[] receiveResponseFrameHeads, byte[] receiveRequestFrameHeads, PortParamCallback portParamCallback) {
        this.mSerialPortParam = new SerialPortParam(devicePath, baudRate);
        this.mSerialPortParam.setResendCount(resendCount);
        this.mSerialPortParam.setSendTimeout(sendTimeout);
        this.mSerialPortParam.setRunTimeout(runTimeout);
        this.mSerialPortParam.setReceiveResponseFrameHeads(receiveResponseFrameHeads);
        this.mSerialPortParam.setReceiveRequestFrameHeads(receiveRequestFrameHeads);
        this.mSerialPortParam.setPortParamCallback(portParamCallback);
    }

}
