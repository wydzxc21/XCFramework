package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口过滤回调
 */
public interface PortFilterCallback {
    /**
     * @param sendDatas    发送数据
     * @param receiveDatas 接收数据
     * @param receiveType  接收类型
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 返回是否相匹配
     */
    boolean onFilter(byte[] sendDatas, byte[] receiveDatas, PortReceiveType receiveType);
}
