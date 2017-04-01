package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.ResponseWriter;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public interface Receiver {

    void register(Message message, ResponseWriter writer) throws IOException;

    void onMessage(Message message);
}
