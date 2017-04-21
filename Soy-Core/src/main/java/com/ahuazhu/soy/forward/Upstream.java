package com.ahuazhu.soy.forward;

import org.xbill.DNS.Message;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public interface Upstream {

    void ask(Message question);

    void onAnswer(Message answer);

    boolean isUdp();

    boolean isTcp();

    boolean isFake();

    String descript();

    boolean establish();

    boolean destroy();

    boolean reconnect();

    boolean lost();
}
