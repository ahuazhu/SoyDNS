package com.ahuazhu.soy.tcp;

import com.ahuazhu.soy.model.ResponseWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by zhengwenzhu on 2017/4/7.
 */
public class TcpNioResponseWriter implements ResponseWriter {

    private SocketChannel channel;

    public TcpNioResponseWriter(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void write(ByteBuffer data) throws IOException {
        channel.write(data);
    }

    @Override
    public void write(byte[] data) throws IOException {
        channel.write(ByteBuffer.wrap(new byte[]{0, 31}));
        channel.write(ByteBuffer.wrap(data));
        channel.close();
    }
}
