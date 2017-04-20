package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.cache.Cache;
import com.ahuazhu.soy.cache.MessageCache;
import com.ahuazhu.soy.modal.QuestionKey;
import com.ahuazhu.soy.modal.ResponseContext;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by zhuzhengwen on 2017/4/19.
 */
public class TcpForwarder implements Forwarder {

    private SocketChannel sc;

    private static Forwarder forwarder;

    private Cache<QuestionKey, Message> cache;

    public static Forwarder getInstance() {
        if (forwarder == null) {
            synchronized (UdpForwarder.class) {
                if (forwarder == null) {
                    TcpForwarder cachedForwarder = new TcpForwarder();
                    cachedForwarder.start();
                    forwarder = cachedForwarder;
                }
            }
        }

        return forwarder;
    }

    private TcpForwarder() {
        cache = new MessageCache();
    }

    @Override
    public void forward(Message message, ResponseContext response) throws IOException {

        Socket clientSocket = new Socket("8.8.8.8", 53);
        byte[] data = message.toWire();
        clientSocket.getOutputStream().write(new byte[]{0, 31});
        clientSocket.getOutputStream().write(data);
        clientSocket.getOutputStream().flush();

        byte[] readData = new byte[512];
        int bytesRcvd = clientSocket.getInputStream().read(readData);
        byte[] answer = Arrays.copyOfRange(readData, 2, bytesRcvd);
        clientSocket.close();
        if (bytesRcvd > 0) {
            response.getWriter().write(answer);
        }
    }


    private void start() {
        try {
            throw new IOException();

        } catch (IOException e) {
            //
        }
    }
}
