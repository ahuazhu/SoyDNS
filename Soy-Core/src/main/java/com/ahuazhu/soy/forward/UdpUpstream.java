package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.utils.Threads;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public class UdpUpstream implements Upstream {

    private String host;

    private int port;

    private DatagramChannel datagramChannel;

    private InetSocketAddress socketAddress;

    public UdpUpstream(String host, int port) {
        this.host = host;
        this.port = port;

        socketAddress = new InetSocketAddress(host, port);
    }

    @Override
    public void ask(Message question) {

        try {
            datagramChannel.send(ByteBuffer.wrap(question.toWire()), socketAddress);

        } catch (IOException e) {
            //
        }
    }

    @Override
    public void onAnswer(Message answer) {

    }

    @Override
    public boolean isUdp() {
        return true;
    }

    @Override
    public boolean isTcp() {
        return false;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    @Override
    public String description() {
        return String.format("[udp-%s:%d]", host, port);
    }

    @Override
    public boolean establish() {
        int tryCount = 100;
        boolean forwarderStarted = false;
        int forwardPort = 7690;

        try {
            datagramChannel = DatagramChannel.open();
            for (int i = 0; i < tryCount && !forwarderStarted; i++) {
                try {
                    datagramChannel.socket().bind(new InetSocketAddress(forwardPort));
                    forwarderStarted = true;
                    System.out.println("Forwarder started on port " + forwardPort);
                } catch (IOException e) {
                    System.err.println("No available");
                    forwardPort += 2;
                }
            }
            Threads.name("UDP-RECEIVE").daemon(this::receive).start();
            return forwarderStarted;
        } catch (IOException e) {
            return false;
        }
    }


    @Override
    public boolean destroy() {
        if (datagramChannel != null && datagramChannel.isConnected()) {
            try {
                datagramChannel.close();
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean reconnect() {
        return true;
    }

    @Override
    public boolean lost() {
        return false;
    }


    private void receive() {
        try {
            Selector selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);

            while (true) {
                try {
                    int n = selector.select();
                    if (n > 0) {
                        Iterator iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = (SelectionKey) iterator.next();
                            iterator.remove();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
                            if (key.isReadable()) {
                                byteBuffer.clear();
                                DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                                datagramChannel.receive(byteBuffer);
                                byteBuffer.flip();
                                byte[] data = byteBuffer.array();
                                byte[] copy = Arrays.copyOf(data, data.length);
                                onAnswer(new Message(copy));
                            }
                        }
                    }
                } catch (ClosedSelectorException | ClosedChannelException e) {
                    System.err.println("Channel closed");

                    break;
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            //ignore
        }
    }
}
