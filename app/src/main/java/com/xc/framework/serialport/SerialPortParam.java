package com.xc.framework.serialport;

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
     * 串口地址地址
     */
    private File device;
    /**
     * 波特率
     */
    private int baudrate;
    /**
     * 数据位，默认8,可选值为5~8
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

    public SerialPortParam(String devicePath, int baudrate) {
        this.device = new File(devicePath);
        this.baudrate = baudrate;
    }

    public SerialPortParam(File device, int baudrate) {
        this.device = device;
        this.baudrate = baudrate;
    }

    public String getSuPath() {
        return suPath;
    }

    public void setSuPath(String suPath) {
        this.suPath = suPath;
    }

    public File getDevice() {
        return device;
    }

    public void setDevice(File device) {
        this.device = device;
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

    @Override
    public String toString() {
        return "SerialPortParam{" +
                "suPath='" + suPath + '\'' +
                ", device=" + device.getAbsolutePath() +
                ", baudrate=" + baudrate +
                ", dataBits=" + dataBits +
                ", stopBits=" + stopBits +
                ", parity=" + parity +
                ", flowCon=" + flowCon +
                '}';
    }
}
