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
        RequestContext request = new RequestContext(query.getQueryData());
        ResponseContext response = new ResponseContext(query.getResponseWriter());

        System.out.println("Submit " + request.getMessage().getQuestion().getName() + " " + System.currentTimeMillis());

        SimpleProcessorChain.create()
                .process(request,
                        response);



    }
}
