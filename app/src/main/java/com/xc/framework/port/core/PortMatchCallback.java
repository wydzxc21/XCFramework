package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口匹配回调
 */
public interface PortMatchCallback {
    /**
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 返回是否相匹配
     */
    boolean onMatch(byte[] sendDatas, byte[] receiveDatas);
}
