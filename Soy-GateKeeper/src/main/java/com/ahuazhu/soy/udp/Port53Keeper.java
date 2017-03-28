package com.ahuazhu.soy.udp;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.xbill.DNS.*;

import java.io.IOException;
import java.net.*;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
@Component
public class Port53Keeper implements InitializingBean {


    public static final int DNS_PORT = 53;

    public static final String LISTENER_IP = "0.0.0.0";

    private static final int UDP_LEN = 512;


    private DatagramSocket socket;

    public void listen() {
        try {
            InetAddress addr = Inet4Address.getByName(LISTENER_IP);
            socket = new DatagramSocket(DNS_PORT, addr);
        } catch (IOException e) {
            System.err.println("Startup fail, 53 port is taken or has no privilege. Check if you are running in root, or another DNS server is running.");
            System.exit(-1);
        }
    }

    private void dispatchWithLoop() {
        for (; ; ) {
            byte[] in = new byte[UDP_LEN];
            DatagramPacket inPacket = new DatagramPacket(in, in.length);
            inPacket.setLength(in.length);
            try {
                socket.receive(inPacket);
                Message query = new Message(inPacket.getData());
                System.out.println(query);


                Message answer = new Message(query.getHeader().getID());
                //Name name, int dclass, long ttl, InetAddress address
                Record question = query.getQuestion();

                Record record = new RecordBuilder()
                        .dclass(question.getDClass())
                        .name(question.getName())
                        .answer("10.11.12.13")
                        .type(question.getType())
                        .toRecord();

                answer.addRecord(record, Section.ANSWER);

                byte[] response = answer.toWire();
                DatagramPacket outPacket = new DatagramPacket(response,
                        response.length, inPacket.getAddress(),
                        inPacket.getPort());

                outPacket.setData(response);
                outPacket.setLength(response.length);
                outPacket.setAddress(inPacket.getAddress());
                outPacket.setPort(inPacket.getPort());
                socket.send(outPacket);

            } catch (SocketException e) {
                System.err.println("Socket error " + e.getMessage());
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        listen();
        Thread thread = new Thread(this::dispatchWithLoop);
        thread.setDaemon(true);
        thread.setName("DNS-Socket-Listener");
        thread.start();
    }
}
