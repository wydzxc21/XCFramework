package com.xc.framework.port.serial;

import com.xc.framework.util.XCByteUtil;
import com.xc.framework.util.XCStringUtil;

import java.io.File;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口参数
 */
public class SerialPortParam {
    /**
     * su路径，默认：/system/bin/su
     */
    private String suPath = "/system/bin/su";
    /**
     * 串口地址
     */
    private File serialDevice;
    /**
     * 波特率
     */
    private int baudrate;
    /**
     * 数据位，默认8，可选值为5~8
     */
    private int dataBits = 8;
    /**
     * 停止位，1:1位停止位(默认)；2:2位停止位
     */
    private int stopBits = 1;
    /**
     * 校验位，0:无校验位(默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
     */
    private int parity = 0;
    /**
     * 流控，0:不使用流控(默认)；1:硬件流控(RTS/CTS)；2:软件流控(XON/XOFF)
     */
    private int flowCon = 0;
    /**
     * 重发次数，默认0，不重发
     */
    int resendCount = 0;
    /**
     * 发送超时(毫秒)，默认2000
     */
    int sendTimeout = 2000;
    /**
     * 接收帧头，默认null，处理丢包粘包
     */
    byte[] receiveFrameHeads;


    public SerialPortParam(File serialDevice, int baudrate) {
        this.serialDevice = serialDevice;
        this.baudrate = baudrate;
    }

    public SerialPortParam(String serialDevicePath, int baudrate) {
        this.serialDevice = new File(serialDevicePath);
        this.baudrate = baudrate;
    }

    public SerialPortParam(String suPath, String serialDevicePath, int baudrate, int dataBits, int stopBits, int parity, int flowCon, int resendCount, int sendTimeout, byte[] receiveFrameHeads) {
        this.suPath = !XCStringUtil.isEmpty(suPath) ? suPath : this.suPath;
        this.serialDevice = new File(serialDevicePath);
        this.baudrate = baudrate;
        this.dataBits = dataBits > 0 ? dataBits : this.dataBits;
        this.stopBits = stopBits > 0 ? stopBits : this.stopBits;
        this.parity = parity > 0 ? parity : this.parity;
        this.flowCon = flowCon > 0 ? flowCon : this.flowCon;
        this.resendCount = resendCount > 0 ? resendCount : this.resendCount;
        this.sendTimeout = sendTimeout > 0 ? sendTimeout : this.sendTimeout;
        this.receiveFrameHeads = receiveFrameHeads;
    }


    public String getSuPath() {
        return suPath;
    }

    public void setSuPath(String suPath) {
        this.suPath = suPath;
    }

    public File getSerialDevice() {
        return serialDevice;
    }

    public void setSerialDevice(File serialDevice) {
        this.serialDevice = serialDevice;
    }

    public void setSerialDevice(String serialDevicePath) {
        this.serialDevice = new File(serialDevicePath);
    }

    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getFlowCon() {
        return flowCon;
    }

    public void setFlowCon(int flowCon) {
        this.flowCon = flowCon;
    }

    public int getResendCount() {
        return resendCount;
    }

    public void setResendCount(int resendCount) {
        this.resendCount = resendCount;
    }

    public int getSendTimeout() {
        return sendTimeout;
    }

    public void setSendTimeout(int sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public byte[] getReceiveFrameHeads() {
        return receiveFrameHeads;
    }

    public void setReceiveFrameHeads(byte[] receiveFrameHeads) {
        this.receiveFrameHeads = receiveFrameHeads;
    }

}
