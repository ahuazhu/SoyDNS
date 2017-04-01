package com.ahuazhu.soy.modal;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class ResponseContext {
    ResponseWriter writer;

    private Throwable error;

    public ResponseContext(ResponseWriter writer) {
        this.writer = writer;
    }

    public ResponseWriter getWriter() {
        return writer;
    }

    public boolean hasError() {
        return error != null;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
