package com.ahuazhu.soy.udp;

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

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
@Component
public class Port53NIOKeeper implements InitializingBean {


    public static final int DNS_PORT = 53;

    public static final String LISTENER_IP = "0.0.0.0";

    private static final int UDP_LEN = 512;

    private DatagramChannel channel;

    private Selector selector;

    public void listen() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(DNS_PORT));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            System.err.println("Soy DNS server started ob port 53");

            ByteBuffer byteBuffer = ByteBuffer.allocate(UDP_LEN);
            while (true) {
                int n = selector.select();
                if (n > 0) {
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                            byteBuffer.clear();
                            // 读取
                            InetSocketAddress address = (InetSocketAddress) datagramChannel.receive(byteBuffer);
//                            System.out.println(new String(byteBuffer.array()));
                            System.out.println(Arrays.toString(byteBuffer.array()));

                            // 删除缓冲区中的数据
//                            byteBuffer.clear();
//
//                            String message = "data come from server";
//                            byteBuffer.put(message.getBytes());
//                            byteBuffer.flip();
//                            datagramChannel.send(byteBuffer, address);

                            Message query = new Message(byteBuffer);
                            System.out.println(query);
                            Message answer = new Message(query.getHeader().getID());
                            Record question = query.getQuestion();
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

//                            DatagramPacket outPacket = new DatagramPacket(answerData,
//                                    answerData.length, inPacket.getAddress(),
//                                    inPacket.getPort());
//
//                            outPacket.setData(answerData);
//                            outPacket.setLength(answerData.length);
//                            outPacket.setAddress(inPacket.getAddress());
//                            outPacket.setPort(inPacket.getPort());
//                            request.getUdpSocket().send(outPacket);
                        }
                    }
                }


            }
        } catch (IOException e) {
            System.err.println("Startup fail, 53 port is taken or has no privilege. Check if you are running in root, or another DNS server is running.");
            System.exit(-1);
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
