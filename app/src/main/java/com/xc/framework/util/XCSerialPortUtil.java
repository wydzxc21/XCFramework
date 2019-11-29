package com.xc.framework.util;

import com.xc.framework.serialport.SerialPortFinder;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：串口工具
 */
public class XCSerialPortUtil {
    static XCSerialPortUtil mXCSerialPortUtil;

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 14:19
     * Description：获取所有端口路径
     */
    public static String[] getAllDevicesPath() {
        return SerialPortFinder.getInstance().getAllDevicesPath();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 14:19
     * Description：获取所有端口名称
     */
    public static String[] getAllDevicesName() {
        return SerialPortFinder.getInstance().getAllDevices();
    }


}
