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

package com.xc.framework.serialport;

import java.io.FileDescriptor;

/**
 * Author：ZhangXuanChen
 * Time：2019/11/25 10:13
 * Description：SerialPort
 */
public final class SerialPort {
    private static final String TAG = "SerialPort";

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
     * Description：打开串口
     *
     * @param absolutePath 串口路径
     * @param baudrate     波特率
     * @param dataBits     数据位，默认8
     * @param stopBits     停止位，默认1
     * @param parity       奇偶校验位，默认0（无校验）
     * @param flowCon      流控，默认0（不使用）
     */
    public static native FileDescriptor open(String absolutePath, int baudrate, int dataBits, int stopBits, int parity, int flowCon);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 13:16
     * Description：关闭串口
     */
    public static native void close();

}
