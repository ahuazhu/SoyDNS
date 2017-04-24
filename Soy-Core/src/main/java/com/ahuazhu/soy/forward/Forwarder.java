package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import com.ahuazhu.soy.processor.ProcessorChain;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public interface Forwarder {

    void forward(Message message, ResponseContext response) throws IOException;

    void forward(Message message, RequestContext request, ResponseContext response, ProcessorChain chain) throws IOException;
}
