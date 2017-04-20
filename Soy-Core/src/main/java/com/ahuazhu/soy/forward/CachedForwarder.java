package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.cache.Cache;
import com.ahuazhu.soy.cache.MessageCache;
import com.ahuazhu.soy.modal.QuestionKey;
import com.ahuazhu.soy.modal.ResponseContext;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhuzhengwen on 2017/4/20.
 */
public class CachedForwarder implements Forwarder {

    private Cache<QuestionKey, Message> cache;

    private Forwarder tcpForwarder;

    private Forwarder udpForwarder;


    public CachedForwarder() {
        cache = new MessageCache();
        tcpForwarder = new TcpForwarder();
        udpForwarder = new UdpForwarder();
    }

    @Override
    public void forward(Message message, ResponseContext response) throws IOException {
        Message answer = cache.getValue(new QuestionKey(message));
        if (answer != null) {
            send(answer, response);
            return;
        }


        udpForwarder.forward(message, response);

    }

    private void send(Message message, ResponseContext responseContext) throws IOException {
        responseContext.getWriter().write(message.toWire());
    }
}
