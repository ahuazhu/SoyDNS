package com.ahuazhu.soy.model;

import java.nio.ByteBuffer;

/**
 * Created by zhengwenzhu on 2017/3/31.
 */
public class SimpleQuery implements Query {

    private ByteBuffer data;

    private ResponseWriter writer;

    public SimpleQuery(ByteBuffer data, ResponseWriter writer) {
        this.data = data;
        this.writer = writer;
    }

    public SimpleQuery(byte[] data, ResponseWriter writer) {
        this.data = ByteBuffer.wrap(data);
        this.writer = writer;
    }

    @Override
    public ByteBuffer getQueryData() {
        return data;
    }

    @Override
    public ResponseWriter getResponseWriter() {
        return writer;
    }
}
