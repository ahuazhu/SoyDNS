package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import com.ahuazhu.soy.modal.ResponseWriter;

/**
 * Created by zhengwenzhu on 2017/4/3.
 */
public class SendProcessor implements Processor {
    @Override
    public void process(RequestContext request, ResponseContext response, ProcessorChain chain) throws SoyException {
        if (response.getResult() != null) {
            send(response);

        }
    }

    private void send(ResponseContext response) {
        try {
            ResponseWriter writer = response.getWriter();
            if (writer != null) {
                writer.write(response.getResult().toWire());
            }
        } catch (Exception e) {
            //
        }
    }
}
