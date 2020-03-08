package com.xc.framework.port.serial;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口监听
 */
public interface OnSerialPortListener {
    /**
     * @return 0或-1：继续读取
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 设置数据有效长度(十进制)
     */
    int setLength(int what, byte[] receiveDatas);

    /**
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 接收
     */
    void onReceive(int what, byte[] receiveDatas);

    /**
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 超时
     */
    void onTimeout(int what, byte[] sendDatas);
}
