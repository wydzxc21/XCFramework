package com.xc.framework.socket.core.iocore.interfaces;

import java.io.Serializable;


/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket-可发送类,继承该类,并实现parse方法即可获得发送能力
 */
public interface ISendable extends Serializable {
    /**
     * 数据转化
     *
     * @return 将要发送的数据的字节数组
     */
    byte[] parse();
}
