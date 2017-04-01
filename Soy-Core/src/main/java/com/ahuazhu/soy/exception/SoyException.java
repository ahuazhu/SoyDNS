package com.ahuazhu.soy.exception;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class SoyException extends RuntimeException {

    public SoyException(String msg, Throwable e) {
        super(msg, e);
    }

    public SoyException(IOException e) {
        super(e);
    }

    public SoyException() {
        super();
    }
}
