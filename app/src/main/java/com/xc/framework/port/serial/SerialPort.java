/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xc.framework.port.serial;

import com.xc.framework.util.XCStringUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Author：ZhangXuanChen
 * Time：2019/11/25 10:13
 * Description：SerialPort
 */
public final class SerialPort {
    private static final String TAG = "SerialPort";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 14:53
     * Description：loadLibrary
     */
    static {
        System.loadLibrary("SerialPort");
    }

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
     * Time：2019/11/27 14:59
     * Description：打开串口
     *
     * @param serialPortParam 串口参数
     */
    public boolean openSerialPort(SerialPortParam serialPortParam) {
        if (serialPortParam == null) {
            return false;
        }
        return openSerialPort(
                serialPortParam.getSuPath(),//su路径，默认：/system/bin/su
                serialPortParam.getSerialDevice(),//串口设备文件
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
        if (!isPermission(suPath, device)) {
            return false;
        }
        mFd = open(device.getAbsolutePath(), baudrate, dataBits, stopBits, parity, flowCon);
        if (mFd == null) {
            return false;
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
        if (mFileInputStream == null || mFileOutputStream == null) {
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
            if (mFileInputStream != null) {
                mFileInputStream.close();
                mFileInputStream = null;
            }
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
                mFileOutputStream = null;
            }
            if (mFd != null) {
                close();
                mFd = null;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 16:13
     * Description：readSerialPort
     * Return：int
     */
    public synchronized byte[] readSerialPort() {
        byte[] bytes = null;
        try {
            if (mFileInputStream != null) {
                byte[] bufferDatas = new byte[1024];
                int readSize = mFileInputStream.read(bufferDatas);
                if (readSize > 0) {
                    bytes = Arrays.copyOf(bufferDatas, readSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 16:10
     * Description：writeSerialPort
     * Return：boolean
     */
    public synchronized boolean writeSerialPort(byte[] bytes) {
        try {
            if (mFileOutputStream != null && bytes != null && bytes.length > 0) {
                mFileOutputStream.write(bytes);
                mFileOutputStream.flush();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 11:43
     * Description：检查权限
     */
    private boolean isPermission(String suPath, File device) {
        if (XCStringUtil.isEmpty(suPath) || device == null || !device.exists()) {
            return false;
        }
        try {
            if (!device.canRead() || !device.canWrite()) {/* Missing read/write permission, trying to chmod the file */
                Process su = Runtime.getRuntime().exec(suPath);
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 13:16
     * Description：打开串口
     *
     * @param absolutePath 串口路径
     * @param baudrate     波特率
     * @param dataBits     数据位，默认8
     * @param stopBits     停止位，默认1
     * @param parity       奇偶校验位，默认0（无校验）
     * @param flowCon      流控，默认0（不使用）
     */
    private native FileDescriptor open(String absolutePath, int baudrate, int dataBits, int stopBits, int parity, int flowCon);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 13:16
     * Description：关闭串口
     */
    private native void close();

}
