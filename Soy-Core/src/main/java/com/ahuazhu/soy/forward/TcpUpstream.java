package com.ahuazhu.soy.forward;

import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public class TcpUpstream implements Upstream {


    private String host;

    private int port;

    private int weight;

    private boolean lost = false;

    private Socket socket;

    public TcpUpstream(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    @Override
    public void ask(Message question) {
        try {
            byte[] data = question.toWire();
            socket.getOutputStream().write(new byte[]{0, 31});
            socket.getOutputStream().write(data);
            socket.getOutputStream().flush();
            byte[] readData = new byte[512];
            int bytesReceived = socket.getInputStream().read(readData);
            byte[] answer = Arrays.copyOfRange(readData, 2, bytesReceived);
            socket.close();
            if (bytesReceived > 0) {
                onAnswer(new Message(answer));
            }
        } catch (IOException e) {
            //
        }
    }

    @Override
    public void onAnswer(Message answer) {
        try {
            socket.getOutputStream().write(answer.toWire());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            //
            lost = true;
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
            socket = new Socket(host, port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean destroy() {
        try {
            socket.close();
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
