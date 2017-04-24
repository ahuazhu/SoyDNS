package com.ahuazhu.soy.forward;

import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    Socket clientSocket;

    public TcpUpstream(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void ask(Message question, AnswerHandler answerHandler) {
        try {

            byte[] data = question.toWire();
            clientSocket.getOutputStream().write(new byte[]{0, 31});
            clientSocket.getOutputStream().write(data);
            clientSocket.getOutputStream().flush();

            byte[] readData = new byte[512];
            int bytesRcvd = clientSocket.getInputStream().read(readData);
            byte[] answer = Arrays.copyOfRange(readData, 2, bytesRcvd);
            if (bytesRcvd > 0) {
                answerHandler.onAnswer(new Message(answer));
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
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port), 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean destroy() {
        try {
            channel.close();
            if (clientSocket != null && clientSocket.isConnected()) {
                clientSocket.close();
            }
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
