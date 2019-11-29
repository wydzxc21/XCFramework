package com.xc.framework.serialport;

import java.io.IOException;
import java.io.InputStream;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口接收线程
 */
public abstract class SerialPortReceivedThread extends Thread {
    private InputStream mInputStream;
    private boolean isRun = false;

    public SerialPortReceivedThread(InputStream inputStream) {
        this.mInputStream = inputStream;
    }

    @Override
    public void run() {
        super.run();
        while (isRun && !isInterrupted()) {
            synchronized (mInputStream) {
                try {
                    byte[] buffer = new byte[4096];
                    int size = read(buffer);
                    if (size > 0) {
                        byte[] buff = java.util.Arrays.copyOfRange(buffer, 0, size);
                        onReceive(buff);
                    } else {
                        Thread.sleep(1);
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
     * Time：2019/11/25 16:13
     * Description：read
     * Return：int
     */
    public synchronized int read(byte[] buffer) {
        int read = -1;
        try {
            if (mInputStream != null) {
                read = mInputStream.read(buffer);
            }
        } catch (IOException e) {
        }
        return read;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onReceive
     */
    public abstract void onReceive(byte[] bytes);
}
