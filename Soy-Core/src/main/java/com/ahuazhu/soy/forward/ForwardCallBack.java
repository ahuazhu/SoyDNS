package com.ahuazhu.soy.forward;

import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/4/7.
 */
public interface ForwardCallBack {

    void onMessage(Message message);
}
