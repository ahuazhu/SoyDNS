package com.ahuazhu.soy.udp;

import com.ahuazhu.soy.Soy;
import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.Request;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

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


    private DatagramSocket udpSocket;

    public void listen() {
        try {
            InetAddress addr = Inet4Address.getByName(LISTENER_IP);
            udpSocket = new DatagramSocket(DNS_PORT, addr);
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
                udpSocket.receive(inPacket);
                Soy.fire(new Request(udpSocket, inPacket));
            } catch (SocketException e) {
                System.err.println("Socket error " + e.getMessage());
                break;
            } catch (IOException | SoyException e) {
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
