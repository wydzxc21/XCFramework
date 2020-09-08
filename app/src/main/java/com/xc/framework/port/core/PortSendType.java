package com.xc.framework.port.core;

/**
 * Date：2020/7/29
 * Author：ZhangXuanChen
 * Description：串口发送类型
 */
public enum PortSendType {
    /**
     * Author：ZhangXuanChen
     * Time：2020/7/29 9:04
     * Description：队列（按消息队列顺序发送）
     */
    Queue,
    /**
     * Author：ZhangXuanChen
     * Time：2020/7/29 9:04
     * Description：自由（不走消息队列即时发送）
     */
    Free,
}
