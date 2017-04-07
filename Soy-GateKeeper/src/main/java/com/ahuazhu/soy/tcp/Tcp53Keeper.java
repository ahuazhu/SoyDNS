package com.ahuazhu.soy.tcp;

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
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by zhengwenzhu on 2017/4/7.
 */
@Component
public class Tcp53Keeper implements InitializingBean {
    private Charset charset = Charset.forName("UTF-8");
    public static final int DNS_PORT = 5553;
    private static final int UDP_LEN = 512;
    private Executor executor = Executors.Sync;


    public void listen() {

        Selector selector = null;
        ServerSocketChannel channel = null;

        try {
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            selector = Selector.open();
            channel.socket().bind(new InetSocketAddress(DNS_PORT));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);

            System.err.println("Soy DNS server started on port " + DNS_PORT);
        } catch (IOException e) {
            System.err.println("Startup fail, 53 port is taken or has no privilege. Check if you are running in root, or another DNS server is running.");
            System.exit(-1);
        }


//        while (true) {
//            try {
//                int n = selector.select();
//                if (n > 0) {
//                    Iterator iterator = selector.selectedKeys().iterator();
//                    while (iterator.hasNext()) {
//                        SelectionKey sk = (SelectionKey) iterator.next();
//                        iterator.remove();
//                        if (sk.isAcceptable()) {
//                            SocketChannel sc = channel.accept();
//                            sc.configureBlocking(false);
//                            sc.register(selector, SelectionKey.OP_READ);
//                            sk.interestOps(SelectionKey.OP_ACCEPT);
//                        }
//
//                        if (sk.isReadable()) {
//                            SocketChannel sc = (SocketChannel) sk.channel();
//                            ByteBuffer buff = ByteBuffer.allocate(512);
//
//                            try {
//                                int bytes = sc.read(buff);
//                                if (bytes > 0) {
//                                    byte[] data = new byte[bytes - 2];
//                                    System.arraycopy(buff.array(), 2, data, 0, data.length);
//                                    Query query = new SimpleQuery(data, new TcpNioResponseWriter(sc));
//                                    executor.execute(query);
//                                }
//                            } catch (IOException e) {
//                                sk.cancel();
//                                if (sk.channel() != null) {
//                                    sk.channel().close();
//                                }
//
//                            }
//                        }
//                    }
//                }
//            } catch (ClosedSelectorException | ClosedChannelException e) {
//                System.err.println("Channel closed");
//                e.printStackTrace();
//                break;
//            } catch (IOException e) {
//                System.err.println(e.getMessage());
//                e.printStackTrace();
//            }
//        }

        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//                for (SelectionKey sk : selector.selectedKeys()) {
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
//                    selector.selectedKeys().remove(sk);
                    iterator.remove();
                    if (sk.isAcceptable()) {
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                        sk.interestOps(SelectionKey.OP_ACCEPT);
                    }

                    if (sk.isReadable()) {
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buff = ByteBuffer.allocate(512);

                        try {
                            int bytes = sc.read(buff);
                            if (bytes > 0) {
                                byte[] data = new byte[bytes - 2];
                                System.arraycopy(buff.array(), 2, data, 0, data.length);
                                Query query = new SimpleQuery(data, new TcpNioResponseWriter(sc));
                                executor.execute(query);
                            }
                        } catch (IOException e) {
                            sk.cancel();
                            if (sk.channel() != null) {
                                sk.channel().close();
                            }

                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.setName("DNS-Tcp-Listener");
        thread.start();
    }

}
