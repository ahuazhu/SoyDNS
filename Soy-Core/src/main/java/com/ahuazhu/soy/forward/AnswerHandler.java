package com.ahuazhu.soy.forward;

import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhuzhengwen on 2017/4/23.
 */
public interface AnswerHandler {

    void onAnswer(Message answer) throws IOException;
}
