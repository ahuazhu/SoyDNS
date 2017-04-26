package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.model.ResponseContext;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/4/7.
 */
public class SendResultForwardCallBack implements ForwardCallBack {

    private ResponseContext responseContext;

    public SendResultForwardCallBack(ResponseContext response) {
        this.responseContext = response;
    }

    @Override
    public void onMessage(Message message) {

        try {
            responseContext.getWriter().write(message.toWire());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
