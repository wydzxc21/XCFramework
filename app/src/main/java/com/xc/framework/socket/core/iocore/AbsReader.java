package com.xc.framework.socket.core.iocore;


import com.xc.framework.socket.core.iocore.interfaces.IIOCoreOptions;
import com.xc.framework.socket.core.iocore.interfaces.IReader;
import com.xc.framework.socket.core.iocore.interfaces.IStateSender;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public abstract class AbsReader implements IReader<IIOCoreOptions> {

    protected volatile IIOCoreOptions mOkOptions;

    protected IStateSender mStateSender;

    protected InputStream mInputStream;

    public AbsReader() {
    }

    @Override
    public void initialize(InputStream inputStream, IStateSender stateSender) {
        mStateSender = stateSender;
        mInputStream = inputStream;
    }

    @Override
    public void setOption(IIOCoreOptions option) {
        mOkOptions = option;
    }


    @Override
    public void close() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }
}
