package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import org.ehcache.Cache;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public class CacheProcessor implements Processor {

    @Override
    public void process(RequestContext request, ResponseContext response, ProcessorChain chain) throws SoyException {
        Cache<String, Message> cache = CacheManager.getCache();

        Message message = request.getMessage();

        String key = message.getQuestion().getName().toString() + "_" + message.getQuestion().getType();

        Message cached = cache.get(key);

        if (cached != null) {
            Message m = (Message) cached.clone();
            m.getHeader().setID(message.getHeader().getID());

            try {
                response.getWriter().write(m.toWire());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            chain.process(request, response);
        }
    }

}
