package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.cache.Cache;
import com.ahuazhu.soy.cache.ForwardCallBackCache;
import com.ahuazhu.soy.modal.QueryKey;
import com.ahuazhu.soy.modal.ResponseContext;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public class UdpForwarder implements Forwarder {
    private int forwardPort = 7690;

    private DatagramChannel datagramChannel;

    private boolean forwarderStarted = false;

    private List<InetSocketAddress> upstreamServers;

    private static Forwarder forwarder;

    private Cache<QueryKey, ForwardCallBack> callBackCache;

    public static Forwarder getInstance() {
        if (forwarder == null) {
            synchronized (UdpForwarder.class) {
                if (forwarder == null) {
                    UdpForwarder udpForwarder = new UdpForwarder();
                    udpForwarder.start();
                    forwarder = udpForwarder;
                }
            }
        }

        return forwarder;
    }

    private UdpForwarder() {
        upstreamServers = new ArrayList<>();
        upstreamServers.add(new InetSocketAddress("8.8.8.8", 53));
        callBackCache = new ForwardCallBackCache();
    }

    private void start() {
        try {
            datagramChannel = DatagramChannel.open();
            int tryCount = 100;
            for (int i = 0; i < tryCount && !forwarderStarted; i++) {
                try {
                    datagramChannel.socket().bind(new InetSocketAddress(forwardPort));
                    forwarderStarted = true;
                    System.out.println("Forwarder started on port " + forwardPort);
                } catch (IOException e) {
                    System.err.println("Port " + forwarder + " not available");
                    forwardPort += 2;
                }
            }

            if (!forwarderStarted) {
                System.err.println("no available port for forwarder, recursive resolution is disable. ");
                return;
            }
            datagramChannel.configureBlocking(false);
            Thread threadForReceive = new Thread(this::receive, "Forwarder-Receiver");
            threadForReceive.setDaemon(true);
            threadForReceive.start();
            Thread threadForRemove = new Thread(this::remove, "Forwarder-Remover");
            threadForRemove.setDaemon(true);
            threadForRemove.start();
        } catch (IOException e) {
            System.err.println("Recursive resolution is disable because of " + e);
        }
    }

    @Override
    public void forward(Message message, ResponseContext response) throws IOException {
        if (forwarderStarted) {
            callBackCache.putValue(new QueryKey(message), new SendResultForwardCallBack(response));
            for (InetSocketAddress upstreamServer : upstreamServers) {
                datagramChannel.send(ByteBuffer.wrap(message.toWire()), upstreamServer);
            }
        }
    }

    public void receive() {
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
                                onMessage(new Message(copy));
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

    private void onMessage(Message message) throws IOException {
        QueryKey key = new QueryKey(message);
        ForwardCallBack callBack = callBackCache.getValue(key);
        if (callBack != null) {
            callBack.onMessage(message);
            callBackCache.remove(key);
        }
    }


    public void remove() {

    }

}
