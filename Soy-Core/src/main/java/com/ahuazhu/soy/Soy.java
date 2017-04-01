package com.ahuazhu.soy;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.Query;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import com.ahuazhu.soy.processor.SimpleProcessorChain;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class Soy {

    public static void fire(Query query) throws SoyException {
        SimpleProcessorChain.create()
                .process(new RequestContext(query.getQueryData()),
                new ResponseContext(query.getResponseWriter()));

    }
}
