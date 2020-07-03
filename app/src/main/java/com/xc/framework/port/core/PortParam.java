package com.xc.framework.port.core;

/**
 * Date：2020/7/3
 * Author：ZhangXuanChen
 * Description：串口参数基类
 */
public class PortParam {
    /**
     * 波特率
     */
    protected int baudrate;
    /**
     * 数据位，默认8，可选值为5~8
     */
    protected int dataBits = 8;
    /**
     * 停止位，1:1位停止位(默认)；2:2位停止位
     */
    protected int stopBits = 1;
    /**
     * 校验位，0:无校验位(默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
     */
    protected int parity = 0;
    /**
     * 重发次数，默认0，不重发
     */
    protected int resendCount = 0;
    /**
     * 发送超时(毫秒)，默认2000
     */
    protected int sendTimeout = 2000;
    /**
     * 接收帧头，默认null，处理丢包粘包
     */
    protected byte[] receiveFrameHeads;
    /**
     * 中断帧头，默认null，处理丢包粘包
     */
    protected byte[] interruptFrameHeads;
    /**
     * 设置接收数据长度回调
     */
    protected LengthCallback lengthCallback;

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

    public byte[] getInterruptFrameHeads() {
        return interruptFrameHeads;
    }

    public void setInterruptFrameHeads(byte[] interruptFrameHeads) {
        this.interruptFrameHeads = interruptFrameHeads;
    }

    public LengthCallback getLengthCallback() {
        return lengthCallback;
    }

    public void setLengthCallback(LengthCallback lengthCallback) {
        this.lengthCallback = lengthCallback;
    }
}
