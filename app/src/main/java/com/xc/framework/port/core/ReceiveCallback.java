package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口接收回调
 */
public interface ReceiveCallback {
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
