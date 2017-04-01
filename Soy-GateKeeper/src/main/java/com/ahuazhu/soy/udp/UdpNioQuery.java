package com.ahuazhu.soy.udp;

import com.ahuazhu.soy.modal.Query;
import com.ahuazhu.soy.modal.ResponseWriter;

import java.nio.ByteBuffer;

/**
 * Created by zhengwenzhu on 2017/3/31.
 */
public class UdpNioQuery implements Query {

    private ByteBuffer data;

    private ResponseWriter writer;

    public UdpNioQuery(ByteBuffer data, ResponseWriter writer) {
        this.data = data;
        this.writer = writer;
    }

    public UdpNioQuery(byte[] data, ResponseWriter writer) {
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
