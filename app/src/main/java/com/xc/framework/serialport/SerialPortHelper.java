package com.xc.framework.serialport;

import com.xc.framework.util.XCStringUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：串口助手
 */
public class SerialPortHelper {
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 13:16
     * Description：getInputStream
     */
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 13:16
     * Description：getOutputStream
     */
    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 11:43
     * Description：检查权限
     */
    private boolean isChmod(String suPath, File device) {
        if (XCStringUtil.isEmpty(suPath) || device == null || !device.exists()) {
            return false;
        }
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec(suPath);
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 14:59
     * Description：打开串口
     * Param：SerialPortParam
     */
    public boolean openSerialPort(SerialPortParam serialPortParam) {
        return openSerialPort(
                serialPortParam.getSuPath(),//su路径，默认：/system/bin/su
                serialPortParam.getDevice(),//串口设备文件
                serialPortParam.getBaudrate(),//波特率
                serialPortParam.getDataBits(),//数据位，默认8
                serialPortParam.getStopBits(),//停止位，默认1
                serialPortParam.getParity(),//奇偶校验位，默认0（无校验）
                serialPortParam.getFlowCon());//流控，默认0（不使用）);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 13:17
     * Description：打开串口
     *
     * @param suPath   su路径，默认：/system/bin/su
     * @param device   串口设备文件
     * @param baudrate 波特率
     * @param dataBits 数据位，默认8
     * @param stopBits 停止位，默认1
     * @param parity   奇偶校验位，默认0（无校验）
     * @param flowCon  流控，默认0（不使用）
     */
    public boolean openSerialPort(String suPath, File device, int baudrate, int dataBits, int stopBits, int parity, int flowCon) {
        if (!isChmod(suPath, device)) {
            return false;
        }
        try {
            mFd = SerialPort.open(device.getAbsolutePath(), baudrate, dataBits, stopBits, parity, flowCon);
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
        } catch (Exception e) {
            return false;
        }
        if (mFd == null || mFileInputStream == null || mFileOutputStream == null) {
            return false;
        }
        return true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 12:33
     * Description：关闭串口
     */
    public boolean closeSerialPort() {
        try {
            if (mFd != null) {
                SerialPort.close();
                mFd = null;
            }
            if (mFileInputStream != null) {
                mFileInputStream.close();
                mFileInputStream = null;
            }
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
                mFileOutputStream = null;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
