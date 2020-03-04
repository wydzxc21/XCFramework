package com.xc.framework.port.usb;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口发送线程
 */
public class UsbPortSendThread extends Thread {
    private UsbPort mUsbPort;
    private byte[] bytes;

    public UsbPortSendThread(UsbPort usbPort, byte[] bytes) {
        this.mUsbPort = usbPort;
        this.bytes = bytes;
    }

    @Override
    public void run() {
        super.run();
        try {
            int write = write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 16:10
     * Description：write
     * Return：boolean
     */
    public  int write(byte[] buffer) {
        if (mUsbPort == null) {
            return -1;
        }
        return mUsbPort.writeUsbPort(buffer);
    }
}
