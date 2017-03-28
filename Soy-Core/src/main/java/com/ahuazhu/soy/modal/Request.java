package com.ahuazhu.soy.modal;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class Request {

    private DatagramSocket udpSocket;

    private DatagramPacket udpPacket;

    public Request(DatagramSocket udpSocket, DatagramPacket udpPacket) {
        this.udpSocket = udpSocket;
        this.udpPacket = udpPacket;
    }

    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public DatagramPacket getUdpPacket() {
        return udpPacket;
    }
}
