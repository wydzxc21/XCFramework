package com.xc.framework.port.usb;

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

    public UsbPortReceivedThread(UsbPort usbPort) {
        this.mUsbPort = usbPort;
        this.bufferDatas = new byte[1024 * 4];
    }

    @Override
    public void run() {
        super.run();
        synchronized (mUsbPort) {
            while (isRun && !isInterrupted()) {
                try {
                    int size = read(bufferDatas);
                    if (size > 0) {
                        byte[] readDatas = java.util.Arrays.copyOf(bufferDatas, size);
                        onReceive(readDatas);
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
