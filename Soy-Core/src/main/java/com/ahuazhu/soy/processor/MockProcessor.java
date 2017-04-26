package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.model.RequestContext;
import com.ahuazhu.soy.model.ResponseContext;
import com.ahuazhu.soy.utils.RecordBuilder;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.TextParseException;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class MockProcessor implements Processor {

    @Override
    public void process(RequestContext request,
                        ResponseContext response,
                        ProcessorChain chain) throws SoyException {
        try {
            Message query = new Message(request.getQueryData());
            byte[] answerData = mock(query);
            response.getWriter().write(answerData);
        } catch (IOException e) {
            throw new SoyException();
        }
        chain.process(request, response);
    }

    private byte[] mock(Message query) throws UnknownHostException, TextParseException {
        Message answer = new Message(query.getHeader().getID());
        Record question = query.getQuestion();
        Record record = new RecordBuilder()
                .dclass(question.getDClass())
                .name(question.getName())
                .answer("10.11.12.13")
                .type(question.getType())
                .toRecord();
        answer.addRecord(record, Section.ANSWER);
        return answer.toWire();
    }
}
