package com.ahuazhu.soy.udp;

import com.ahuazhu.soy.executor.Executor;
import com.ahuazhu.soy.executor.Executors;
import com.ahuazhu.soy.modal.Query;
import com.ahuazhu.soy.modal.SimpleQuery;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
@Component
public class Udp53Keeper implements InitializingBean {


    public static final int DNS_PORT = 53;

    private static final int UDP_LEN = 512;

    private Executor executor = Executors.Disruptor;

    public void listen() {

        Selector selector = null;
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(DNS_PORT));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            System.err.println("Soy DNS server started on port " + DNS_PORT);
        } catch (IOException e) {
            System.err.println("Startup fail, 53 port is taken or has no privilege. Check if you are running in root, or another DNS server is running.");
            System.exit(-1);
        }

        while (true) {
            try {
                int n = selector.select();
                if (n > 0) {
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(UDP_LEN);
                        if (key.isReadable()) {
                            byteBuffer.clear();
                            DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                            InetSocketAddress address = (InetSocketAddress) datagramChannel.receive(byteBuffer);
                            byte[] data = Arrays.copyOf(byteBuffer.array(), byteBuffer.position());
                            byteBuffer.clear();
                            Query query = new SimpleQuery(data, new UdpNioResponseWriter(datagramChannel, address));
                            executor.execute(query);

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
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.setName("DNS-Socket-Listener");
        thread.start();
    }
}
