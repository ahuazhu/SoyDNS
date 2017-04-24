package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.cache.Cache;
import com.ahuazhu.soy.modal.QuestionKey;
import com.ahuazhu.soy.modal.ResponseWriter;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhuzhengwen on 2017/4/23.
 */
public class WriteHandler implements AnswerHandler {

    public ResponseWriter writer;

    private Cache<QuestionKey, Message> questionCache;

    public WriteHandler(ResponseWriter writer) {
        this.writer = writer;
    }

    @Override
    public void onAnswer(Message answer) throws IOException {
        questionCache.putValue(new QuestionKey(answer), answer);
        writer.write(answer.toWire());

    }

    public void setQuestionCache(Cache<QuestionKey, Message> questionCache) {
        this.questionCache = questionCache;
    }
}
