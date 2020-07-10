package com.xc.framework.port.serial;


import com.xc.framework.port.core.PortReceiveThread;

/**
 * Date：2020/3/10
 * Author：ZhangXuanChen
 * Description：串口接收
 */
public abstract class SerialPortReceiveThread extends PortReceiveThread {
    private SerialPort serialPort;

    /**
     * @param serialPortParam 串口参数
     * @param serialPort      串口工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public SerialPortReceiveThread(SerialPortParam serialPortParam, SerialPort serialPort) {
        super(serialPortParam);
        this.serialPort = serialPort;
    }

    @Override
    protected byte[] readPort() {
        return serialPort.readSerialPort();
    }
}
