package com.xc.framework.port.usb;

import java.util.Arrays;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口接收线程
 */
public abstract class UsbPortReceivedThread extends Thread {
    private final String TAG = "UsbPortReceivedThread";
    private UsbPort mUsbPort;
    private boolean isRun = false;
    private byte[] bufferDatas;//缓存数据
    private byte[] completeDatas;//完整数据
    private int completePosition = 0;//数据索引

    public UsbPortReceivedThread(UsbPort usbPort) {
        this.mUsbPort = usbPort;
        this.bufferDatas = new byte[1024];
        this.completeDatas = new byte[mUsbPort != null ? mUsbPort.getMaxPacketSize() : 16 * 1024];
    }

    @Override
    public void run() {
        super.run();
        synchronized (mUsbPort) {
            while (isRun && !isInterrupted()) {
                try {
                    int size = read(bufferDatas);
                    if (size > 0) {//开始读取
                        byte[] readDatas = java.util.Arrays.copyOf(bufferDatas, size);
                        System.arraycopy(readDatas, 0, completeDatas, completePosition, readDatas.length);
                        completePosition = completePosition + readDatas.length;
                    } else if (completePosition > 0) {//读取结束
                        onReceive(Arrays.copyOf(completeDatas, completePosition));
                        completePosition = 0;
                    }
                    Thread.sleep(100);
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
        if (mUsbPort == null) {
            return -1;
        }
        return mUsbPort.readUsbPort(buffer);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onReceive
     */
    public abstract void onReceive(byte[] bytes);
}
