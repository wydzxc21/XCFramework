package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口长度回调
 */
public interface LengthCallback {
    /**
     * @return 0或-1：继续读取
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 返回数据有效长度(10进制)
     */
    int onLength(byte[] receiveOrInterruptDatas);
}
