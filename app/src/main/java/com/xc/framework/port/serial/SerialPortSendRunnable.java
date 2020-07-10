package com.xc.framework.port.serial;


import com.xc.framework.port.core.PortSendRunnable;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口发送
 */
public abstract class SerialPortSendRunnable extends PortSendRunnable {
    private final String TAG = "SerialPortSendRunnable";
    private SerialPort serialPort;

    /**
     * @param sendDatas               发送数据
     * @param what                    区分消息
     * @param isWaitResponse          是否等待响应
     * @param serialPortParam         串口参数
     * @param serialPort              串口工具
     * @param serialPortReceiveThread 接收线程
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public SerialPortSendRunnable(byte[] sendDatas, int what, boolean isWaitResponse, SerialPortParam serialPortParam, SerialPort serialPort, SerialPortReceiveThread serialPortReceiveThread) {
        super(sendDatas, what, isWaitResponse, serialPortParam, serialPortReceiveThread);
        this.serialPort = serialPort;
    }

    @Override
    protected boolean writePort(byte[] sendDatas) {
        return serialPort.writeSerialPort(sendDatas);
    }
}
