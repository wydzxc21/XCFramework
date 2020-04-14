package com.xc.framework.socket.common.utils;


import com.xc.framework.socket.core.utils.SLog;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public class SPIUtils {

    public static <E> E load(Class<E> clz) {
        if (clz == null) {
            SLog.e("load null clz error!");
            return null;
        }
        ServiceLoader<E> serviceLoader = ServiceLoader.load(clz, clz.getClassLoader());
        Iterator<E> it = serviceLoader.iterator();
        try {
            if (it.hasNext()) {
                E service = it.next();
                return service;
            }
        } catch (Throwable throwable) {
            SLog.e("load " + clz.getSimpleName() + " error! " + throwable.getMessage());
        }
        return null;
    }
}
