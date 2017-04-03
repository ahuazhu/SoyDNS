package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.modal.QueryKey;
import com.ahuazhu.soy.modal.ResponseContext;
import com.ahuazhu.soy.modal.ResponseWriter;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public class DefaultForwarder implements Forwarder {
    private int forwardPort = 7690;

    private DatagramChannel datagramChannel;

    private boolean forwarderStarted = false;

    private List<InetSocketAddress> upstreamServers;

    private static Forwarder forwarder;

    private Map<QueryKey, ResponseContext> cache;

    public static Forwarder getInstance() {
        if (forwarder == null) {
            synchronized (DefaultForwarder.class) {
                if (forwarder == null) {
                    DefaultForwarder defaultForwarder = new DefaultForwarder();
                    defaultForwarder.start();
                    forwarder = defaultForwarder;
                }
            }
        }

        return forwarder;
    }

    private DefaultForwarder() {
        upstreamServers = new ArrayList<>();
        upstreamServers.add(new InetSocketAddress("8.8.8.8", 53));

        cache = new HashMap<>();
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

            cache.put(QueryKey.of(message), response);

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
        ResponseContext response = cache.get(QueryKey.of(message));
        response.response(message);
    }

    public void remove() {

    }

}
