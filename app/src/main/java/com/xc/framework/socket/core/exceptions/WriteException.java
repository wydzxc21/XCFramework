package com.xc.framework.socket.core.exceptions;


/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket-写异常
 */
public class WriteException extends RuntimeException {
    public WriteException() {
        super();
    }

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteException(Throwable cause) {
        super(cause);
    }

    protected WriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
