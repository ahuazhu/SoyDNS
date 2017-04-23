package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.utils.Schedule;
import com.ahuazhu.soy.utils.Threads;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public class TcpUpstream implements Upstream {


    private String host;

    private int port;

    private boolean lost = false;

    SocketChannel channel = null;

    private AnswerHandler handler;

    public TcpUpstream(String host, int port, AnswerHandler answerHandler) {
        this.host = host;
        this.port = port;
        this.handler = answerHandler;
    }

    @Override
    public void ask(Message question) {
        try {
            ByteBuffer bytesSend = ByteBuffer.allocate(1024);
            byte[] questionData = question.toWire();
            byte[] head = new byte[]{0, 32};
            bytesSend.put(head).put(questionData);
            channel.write(bytesSend);

            ByteBuffer bytesRead = ByteBuffer.allocate(1024);
            if (channel.read(bytesRead) > 0) {
                byte[] data = bytesRead.array();
                byte[] answer = Arrays.copyOfRange(data, 2, data.length);
                handler.onAnswer(new Message(answer));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isUdp() {
        return false;
    }

    @Override
    public boolean isTcp() {
        return true;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    @Override
    public String description() {
        return String.format("[tcp-%s:%d]", host, port);
    }

    @Override
    public boolean establish() {

        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            SocketAddress server = new InetSocketAddress(host, port);
            if (!channel.connect(server)) {
                waitConnected(channel);
            }

            if (channel.finishConnect()) {
                System.out.println("Connected " + toString());
            } else {
                System.out.println("Disconnected " + toString());
            }
        } catch (IOException e) {
            //
        }

        return true;
    }

    private void waitConnected(SocketChannel channel) {
        Schedule.retry(o -> {
            Threads.sleep(10);
            try {
                return channel.finishConnect();
            } catch (IOException e) {
                return false;
            }
        }, 20);
    }

    @Override
    public boolean destroy() {
        try {
            channel.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean reconnect() {

        lost = false;
        return true;
    }

    @Override
    public boolean lost() {
        return lost;
    }
}
