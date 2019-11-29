package com.xc.framework.serialport;

import java.io.OutputStream;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口发送线程
 */
public  class SerialPortSendThread extends Thread {
    private OutputStream mOutputStream;
    private byte[] bytes;
    private boolean isRun = false;

    public SerialPortSendThread(OutputStream outputStream, byte[] bytes) {
        this.mOutputStream = outputStream;
        this.bytes = bytes;
    }

    @Override
    public void run() {
        super.run();
        while (isRun && !isInterrupted()) {
            synchronized (mOutputStream) {
                try {
                    write(bytes);
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
        try {
            if (mOutputStream != null && buffer != null && buffer.length > 0) {
                mOutputStream.write(buffer);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
