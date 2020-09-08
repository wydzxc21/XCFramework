package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口过滤回调
 */
public interface PortFilterCallback {
    /**
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 返回是否相匹配
     */
    boolean onFilter(byte[] sendDatas, byte[] receiveDatas);
}
