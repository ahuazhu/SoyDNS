package com.ahuazhu.soy.modal;

import com.ahuazhu.soy.exception.SoyException;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class RequestContext {


    private ByteBuffer queryData;

    private final Message message;

    private long startMillis;

    private long timeoutMillis;

    public RequestContext(ByteBuffer data) {
        this.queryData = data;
        this.startMillis = System.currentTimeMillis();
        this.timeoutMillis = 5000L;

        try {
            message = new Message(data);
        } catch (IOException e) {
            throw new SoyException("Illegal query", e);
        }
    }

    public ByteBuffer getQueryData() {
        return queryData;
    }

    public Message getMessage() {
        return message;
    }

    public boolean hasTimeout() {
        return System.currentTimeMillis() >= startMillis + timeoutMillis;
    }

}
