package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.forward.DefaultForwarder;
import com.ahuazhu.soy.forward.Forwarder;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public class ForwardProcessor implements Processor {
    @Override
    public void process(RequestContext request, ResponseContext response, ProcessorChain chain) throws SoyException {
        if (response.getResult() == null) {

            Forwarder forwarder = DefaultForwarder.getInstance();
            try {
                Message message = request.getMessage();
                forwarder.forward(message, response);
            } catch (IOException e) {
                response.setError(e);
            }
        }

        chain.process(request, response);
    }

}
