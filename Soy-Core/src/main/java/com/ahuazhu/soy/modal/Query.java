package com.ahuazhu.soy.modal;

import java.nio.ByteBuffer;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public interface Query {

    ByteBuffer getQueryData();

    ResponseWriter getResponseWriter();

}
