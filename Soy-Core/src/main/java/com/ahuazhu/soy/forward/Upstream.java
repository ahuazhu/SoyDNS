package com.ahuazhu.soy.forward;

import org.xbill.DNS.Message;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public interface Upstream {

    void ask(Message question);

    boolean isUdp();

    boolean isTcp();

    boolean isFake();

    String description();

    boolean establish();

    boolean destroy();

    boolean reconnect();

    boolean lost();
}
