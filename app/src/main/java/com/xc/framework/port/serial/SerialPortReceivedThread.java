package com.xc.framework.port.serial;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口接收线程
 */
public abstract class SerialPortReceivedThread extends Thread {
    private SerialPort mSerialPort;
    private boolean isRun = false;

    public SerialPortReceivedThread(SerialPort serialPort) {
        this.mSerialPort = serialPort;
    }

    @Override
    public void run() {
        super.run();
        while (isRun && !isInterrupted()) {
            synchronized (mSerialPort) {
                try {
                    byte[] buffer = new byte[4096];
                    int size = read(buffer);
                    if (size > 0) {
                        byte[] buff = java.util.Arrays.copyOfRange(buffer, 0, size);
                        onReceive(buff);
                    }
                    Thread.sleep(1);
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
    public synchronized int read(byte[] buffer) {
        if (mSerialPort == null) {
            return -1;
        }
        return mSerialPort.readSerialPort(buffer);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onReceive
     */
    public abstract void onReceive(byte[] bytes);
}
