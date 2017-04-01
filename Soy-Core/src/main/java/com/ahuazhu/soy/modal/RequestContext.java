package com.ahuazhu.soy.modal;

import java.nio.ByteBuffer;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class RequestContext {


    ByteBuffer queryData;

    public RequestContext(ByteBuffer data) {
        this.queryData = data;
    }

    public ByteBuffer getQueryData() {
        return queryData;
    }
}
