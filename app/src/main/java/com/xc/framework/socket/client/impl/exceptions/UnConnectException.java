package com.xc.framework.socket.client.impl.exceptions;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public class UnConnectException extends RuntimeException {
    public UnConnectException() {
        super();
    }

    public UnConnectException(String message) {
        super(message);
    }

    public UnConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnConnectException(Throwable cause) {
        super(cause);
    }

    protected UnConnectException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
