package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.modal.ResponseWriter;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhuzhengwen on 2017/4/23.
 */
public class WriteHandler implements AnswerHandler {

    public ResponseWriter writer;

    public WriteHandler(ResponseWriter writer) {
        this.writer = writer;
    }

    @Override
    public void onAnswer(Message answer) throws IOException {
        writer.write(answer.toWire());
    }
}
