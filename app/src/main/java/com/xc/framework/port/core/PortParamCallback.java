package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口参数回调
 */
public interface PortParamCallback {
    /**
     * @param receiveDatas 接收数据
     * @return 0或-1：继续读取
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 返回数据有效长度(10进制)
     */
    int onLength(byte[] receiveDatas);

    /**
     * @param receiveDatas 接收数据
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 返回是否为结果请求
     */
    boolean onResult(byte[] receiveDatas);
}
