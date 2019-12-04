package com.xc.framework.port.usb;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口发送线程
 */
public class UsbPortSendThread extends Thread {
    private UsbPort mUsbPort;
    private byte[] bytes;
    private final int timeout = 5 * 1000;
    private boolean isRun = false;

    public UsbPortSendThread(UsbPort usbPort, byte[] bytes) {
        this.mUsbPort = usbPort;
        this.bytes = bytes;
    }

    @Override
    public void run() {
        super.run();
        while (isRun && !isInterrupted()) {
            synchronized (mUsbPort) {
                try {
                    int write = write(bytes,timeout);
                    if (write > 0) {
                        stopThread();
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
    public synchronized int write(byte[] buffer, int timeout ) {
        if (mUsbPort == null) {
            return -1;
        }
        return mUsbPort.writeUsbPort(buffer,timeout);
    }
}
