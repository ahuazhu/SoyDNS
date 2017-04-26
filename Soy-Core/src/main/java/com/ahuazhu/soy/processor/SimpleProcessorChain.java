package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.model.RequestContext;
import com.ahuazhu.soy.model.ResponseContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class SimpleProcessorChain implements ProcessorChain {

    private static ForwardProcessor forwardProcessor = new ForwardProcessor();

    public static SimpleProcessorChain create() {
        SimpleProcessorChain chain = new SimpleProcessorChain();
        chain.addProcessor(forwardProcessor);
        return chain;
    }

    private List<Processor> processorList = new ArrayList<>();

    private Iterator<Processor> iterator;

    @Override
    public ProcessorChain addProcessor(Processor p) {
        processorList.add(p);
        return this;
    }

    @Override
    public void process(RequestContext request, ResponseContext response) throws SoyException {
        if (iterator == null) {
            iterator = processorList.iterator();
        }

        if (iterator.hasNext() && !request.hasTimeout()) {
            iterator.next().process(request, response, this);
        }
    }
}
