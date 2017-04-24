package com.ahuazhu.soy.utils;

/**
 * Created by zhengwenzhu on 2017/4/5.
 */
public class Constants {
    public static final class RUNTIME {
        public final static class EXECUTE {
            public static final String MODE_SYNC = "sync";
            public static final String MODE_THREAD_POOL = "threadpool";
            public static final String MODE_ACTOR = "actor";
            public static final String MODE_DISRUPTOR = "disruptor";
        }
    }

    public static final class SYSTEM {
        public static final class CACHE {
            public static final long ENTITIES = 1000 * 100;
            public static final long EXPIRE_MILLIS = 1000 * 60 * 10;  // 1min
        }
    }

}
