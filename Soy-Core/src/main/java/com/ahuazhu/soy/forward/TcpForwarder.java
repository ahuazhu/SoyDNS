package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.modal.ResponseContext;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by zhuzhengwen on 2017/4/19.
 */
public class TcpForwarder implements Forwarder {


    private ForwardCallBack forwardCallBack;

    TcpForwarder(ForwardCallBack callBack) {
        this.forwardCallBack = callBack;
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
}
