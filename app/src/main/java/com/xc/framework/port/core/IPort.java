package com.xc.framework.port.core;

/**
 * Date：2020/7/13
 * Author：ZhangXuanChen
 * Description：串口接口
 */
public interface IPort {
    /**
     * Author：ZhangXuanChen
     * Time：2020/7/13 8:07
     * Description：打开串口
     * Param：portParam 串口参数
     */
    boolean openPort(PortParam portParam);

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/13 8:07
     * Description：关闭串口
     */
    boolean closePort();

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/13 7:48
     * Description：读取串口
     */
    byte[] readPort();

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/13 7:48
     * Description：写入串口
     */
    boolean writePort(byte[] bytes);
}
