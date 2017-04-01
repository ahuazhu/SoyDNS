package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.modal.ResponseWriter;
import org.xbill.DNS.Message;

import java.io.IOException;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public interface Forwarder {

    void forward(Message message, ResponseWriter resultWriter) throws IOException;
}
