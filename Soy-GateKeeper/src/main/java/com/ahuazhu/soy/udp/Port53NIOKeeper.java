package com.ahuazhu.soy.udp;

import com.ahuazhu.soy.executor.Executor;
import com.ahuazhu.soy.executor.Executors;
import com.ahuazhu.soy.modal.Query;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
@Component
public class Port53NIOKeeper implements InitializingBean {


    public static final int DNS_PORT = 5553;

    private static final int UDP_LEN = 512;


    private final static ExecutorService es = ExecutorUtils.newBlockingExecutors(1);

    private Executor executor = Executors.SyncExecutor;

    public void listen() {

        Selector selector = null;
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(DNS_PORT));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            System.err.println("Soy DNS server started ob port 53");
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
                            byteBuffer.flip();
                            Query query = new UdpNioQuery(byteBuffer, new UdpNioResponseWriter(datagramChannel, address));
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
