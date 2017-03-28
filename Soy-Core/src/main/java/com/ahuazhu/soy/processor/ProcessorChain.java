package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public interface ProcessorChain {

    ProcessorChain addProcessor(Processor p);

    void process(RequestContext requestContext, ResponseContext responseContext) throws SoyException;

}
