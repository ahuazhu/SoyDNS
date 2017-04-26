package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.cache.Cache;
import com.ahuazhu.soy.cache.MessageCache;
import com.ahuazhu.soy.cache.UdpCallBackCache;
import com.ahuazhu.soy.model.QuestionKey;
import com.ahuazhu.soy.model.RequestContext;
import com.ahuazhu.soy.model.ResponseContext;
import com.ahuazhu.soy.processor.ProcessorChain;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzhengwen on 2017/4/20.
 */
public class CachedForwarder implements Forwarder {

    private Cache<QuestionKey, Message> cache;

    private List<UdpUpstream> upstreams;

    public CachedForwarder() {
        cache = new MessageCache();

        upstreams = new ArrayList<>();

        upstreams.add(new UdpUpstream("114.114.114.114", 53));
        upstreams.add(new UdpUpstream("8.8.8.8", 53));
        upstreams.add(new UdpUpstream("10.65.1.1", 53));
        upstreams.add(new UdpUpstream("8.8.4.4", 53));
        upstreams.add(new UdpUpstream("218.108.248.200", 53));

        UdpCallBackCache callBackCache = new UdpCallBackCache();

        upstreams.forEach(udpUpstream -> {udpUpstream.setCallBackCache(callBackCache); udpUpstream.establish();});
    }

    @Override
    public void forward(Message message, ResponseContext response) throws IOException {
        Message answer = cache.getValue(new QuestionKey(message));
        if (answer != null) {
            send(answer, response);
            return;
        }
        WriteHandler answerHandler = new WriteHandler(response.getWriter());
        answerHandler.setQuestionCache(cache);
//        upstream.ask(message, answerHandler);

        upstreams.stream().parallel().forEach(upstream -> upstream.ask(message, answerHandler));
    }

    @Override
    public void forward(Message message, RequestContext request, ResponseContext response, ProcessorChain chain) throws IOException {
        Message answer = cache.getValue(new QuestionKey(message));
        if (answer != null) {
            send(answer, response);
            return;
        }
        DefaultAnswerHandler answerHandler = new DefaultAnswerHandler(request, response, chain);
        answerHandler.setQuestionCache(cache);
//        upstream.ask(message, answerHandler);

        upstreams.stream().parallel().forEach(upstream -> upstream.ask(message, answerHandler));

    }

    private void send(Message message, ResponseContext responseContext) throws IOException {
        responseContext.getWriter().write(message.toWire());
    }
}
