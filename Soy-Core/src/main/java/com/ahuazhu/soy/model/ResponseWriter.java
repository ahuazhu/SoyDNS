package com.ahuazhu.soy.model;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zhengwenzhu on 2017/3/31.
 */
public interface ResponseWriter {

    void write(ByteBuffer data) throws IOException;

    void write(byte[] data) throws IOException;
}
