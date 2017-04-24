package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.cache.Cache;
import com.ahuazhu.soy.modal.QuestionKey;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import com.ahuazhu.soy.processor.ProcessorChain;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhuzhengwen on 2017/4/24.
 */
public class DefaultAnswerHandler implements AnswerHandler {


    private RequestContext requestContext;

    private ResponseContext responseContext;

    private ProcessorChain chain;

    private Cache<QuestionKey, Message> questionCache;


    public DefaultAnswerHandler(RequestContext requestContext, ResponseContext responseContext, ProcessorChain chain) {
        this.requestContext = requestContext;
        this.responseContext = responseContext;
        this.chain = chain;

    }

    @Override
    public void onAnswer(Message answer) throws IOException {

//        System.out.println("Response " + answer.getQuestion().getName() + " " + System.currentTimeMillis());
        questionCache.putValue(new QuestionKey(answer), answer);

        if (requestContext.hasTimeout()) {
            System.err.println("Timeout for " + answer.getQuestion().getName() + " "+ answer.getHeader().getID());
        } else {
//            System.out.println("SendAnswer " + answer.getQuestion().getName() + " " + System.currentTimeMillis());

            responseContext.getWriter().write(answer.toWire());
        }

//        chain.process(requestContext, responseContext);
    }

    public void setQuestionCache(Cache<QuestionKey, Message> questionCache) {
        this.questionCache = questionCache;
    }
}
