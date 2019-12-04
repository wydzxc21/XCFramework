package com.xc.framework.port.serial;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口发送线程
 */
public class SerialPortSendThread extends Thread {
    private SerialPort mSerialPort;
    private byte[] bytes;
    private boolean isRun = false;

    public SerialPortSendThread(SerialPort serialPort, byte[] bytes) {
        this.mSerialPort = serialPort;
        this.bytes = bytes;
    }

    @Override
    public void run() {
        super.run();
        while (isRun && !isInterrupted()) {
            synchronized (mSerialPort) {
                try {
                    boolean isWrite = write(bytes);
                    if (isWrite) {
                        stopThread();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:16
     * Description：startThread
     */
    public void startThread() {
        isRun = true;
        setDaemon(true);
        start();
    }

    public void stopThread() {
        isRun = false;
        interrupt();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 16:10
     * Description：write
     * Return：boolean
     */
    public synchronized boolean write(byte[] buffer) {
        if (mSerialPort == null) {
            return false;
        }
        return mSerialPort.writeSerialPort(buffer);
    }
}
