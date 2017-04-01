package com.ahuazhu.soy.udp;

import com.ahuazhu.soy.modal.ResponseWriter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by zhengwenzhu on 2017/3/31.
 */
public class UdpNioResponseWriter implements ResponseWriter {

    private DatagramChannel channel;

    private InetSocketAddress address;

    public UdpNioResponseWriter(DatagramChannel channel, InetSocketAddress address) {
        this.channel = channel;
        this.address = address;
    }

    @Override
    public void write(ByteBuffer data) throws IOException {
        channel.send(data, address);
    }

    @Override
    public void write(byte[] data) throws IOException {
        channel.send(ByteBuffer.wrap(data), address);
    }
}
