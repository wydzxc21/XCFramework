package com.xc.framework.port.serial;

import com.xc.framework.port.core.PortParam;
import com.xc.framework.port.core.PortParamCallback;
import com.xc.framework.util.XCStringUtil;

import java.io.File;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：串口参数
 */
public class SerialPortParam extends PortParam {
    /**
     * su路径，默认：/system/bin/su
     */
    private String suPath = "/system/bin/su";
    /**
     * 串口地址
     */
    private File serialDevice;
    /**
     * 流控，0:不使用流控(默认)；1:硬件流控(RTS/CTS)；2:软件流控(XON/XOFF)
     */
    private int flowCon = 0;

    public SerialPortParam(File serialDevice, int baudRate) {
        this.serialDevice = serialDevice;
        this.baudRate = baudRate;
    }

    public SerialPortParam(String serialDevicePath, int baudRate) {
        this.serialDevice = new File(serialDevicePath);
        this.baudRate = baudRate;
    }

    public SerialPortParam(String suPath, String serialDevicePath, int baudRate, int dataBits, int stopBits, int parity, int flowCon, int resendCount, int sendTimeout, int interruptTimeout, byte[] receiveResponseFrameHeads, byte[] receiveRequestFrameHeads, PortParamCallback portParamCallback) {
        this.suPath = !XCStringUtil.isEmpty(suPath) ? suPath : this.suPath;
        this.serialDevice = new File(serialDevicePath);
        this.baudRate = baudRate;
        this.dataBits = dataBits > 0 ? dataBits : this.dataBits;
        this.stopBits = stopBits > 0 ? stopBits : this.stopBits;
        this.parity = parity > 0 ? parity : this.parity;
        this.flowCon = flowCon > 0 ? flowCon : this.flowCon;
        this.resendCount = resendCount > 0 ? resendCount : this.resendCount;
        this.sendTimeout = sendTimeout > 0 ? sendTimeout : this.sendTimeout;
        this.interruptTimeout = interruptTimeout > 0 ? interruptTimeout : this.interruptTimeout;
        this.receiveResponseFrameHeads = receiveResponseFrameHeads;
        this.receiveRequestFrameHeads = receiveRequestFrameHeads;
        this.portParamCallback = portParamCallback;
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

    public int getFlowCon() {
        return flowCon;
    }

    public void setFlowCon(int flowCon) {
        this.flowCon = flowCon;
    }


}
