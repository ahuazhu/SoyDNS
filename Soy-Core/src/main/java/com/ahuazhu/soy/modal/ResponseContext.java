package com.ahuazhu.soy.modal;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class ResponseContext {
    ResponseWriter writer;

    public ResponseContext(ResponseWriter writer) {
        this.writer = writer;
    }

    public ResponseWriter getWriter() {
        return writer;
    }
}
