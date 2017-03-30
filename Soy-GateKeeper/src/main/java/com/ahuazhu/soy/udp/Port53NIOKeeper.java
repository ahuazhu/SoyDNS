package com.ahuazhu.soy.udp;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.ahuazhu.soy.Soy;
import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.Request;
import com.ahuazhu.soy.utils.RecordBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
@Component
public class Port53NIOKeeper implements InitializingBean {


    public static final int DNS_PORT = 5553;

    public static final String LISTENER_IP = "0.0.0.0";

    private static final int UDP_LEN = 512;


//    private final static ExecutorService es = Executors.newFixedThreadPool(10);

    public void listen() {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(DNS_PORT));
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            System.err.println("Soy DNS server started ob port 53");

            ActorSystem system = ActorSystem.create("actor-demo-java");
            ActorRef hello = system.actorOf(Props.create(Hello.class));

            while (true) {
                int n = selector.select();
                if (n > 0) {
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
//                            es.submit(() -> {
//                                process(key);
                            hello.tell(key, hello);
//                                return null;
//                            });

                        }
                    }
                }


            }
        } catch (IOException e) {
            System.err.println("Startup fail, 53 port is taken or has no privilege. Check if you are running in root, or another DNS server is running.");
            System.exit(-1);
        }

    }

    private static class Hello extends AbstractActor {

        @Override
        public Receive createReceive() {
            return ReceiveBuilder.create().match(SelectionKey.class, Port53NIOKeeper::process).build();
        }
    }

    private static void process(SelectionKey key) throws IOException {
        DatagramChannel datagramChannel = (DatagramChannel) key.channel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(UDP_LEN);
        byteBuffer.clear();
        // 读取
        InetSocketAddress address = (InetSocketAddress) datagramChannel.receive(byteBuffer);

        Message query = new Message(byteBuffer.array());
//                            System.out.println(query);
        Message answer = new Message(query.getHeader().getID());
        Record question = query.getQuestion();
        if (question == null) return;
        Record record = new RecordBuilder()
                .dclass(question.getDClass())
                .name(question.getName())
                .answer("10.11.12.13")
                .type(question.getType())
                .toRecord();

        answer.addRecord(record, Section.ANSWER);

        byte[] answerData = answer.toWire();

        byteBuffer.clear();
        byteBuffer.put(answerData);
        byteBuffer.flip();
        datagramChannel.send(byteBuffer, address);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(this::listen);
        thread.setDaemon(true);
        thread.setName("DNS-Socket-Listener");
        thread.start();
    }
}
