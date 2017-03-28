package com.ahuazhu.soy.modal;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class RequestContext {
    private DatagramSocket udpSocket;

    private DatagramPacket udpPacket;

    private Request request;

    public RequestContext(Request request) {
        this.request = request;
        this.udpPacket = request.getUdpPacket();
        this.udpSocket = request.getUdpSocket();
    }

    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public DatagramPacket getUdpPacket() {
        return udpPacket;
    }

    public Request getRequest() {
        return request;
    }
}
