package com.ahuazhu.soy.cache;

import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/4/4.
 */
public interface Cache {

    Message getMessage(Message question);

}
