package com.ahuazhu.soy.config;

import com.ahuazhu.soy.utils.Constants;

/**
 * Created by zhengwenzhu on 2017/4/5.
 */
public interface Configure {

    default String getExecutorModal() {
        return Constants.RUNTIME.EXECUTE.MODE_SYNC;
    }

    default int getThreadPoolCoreSize() {
        return 5;
    }

    default int getThreadPoolMaxSize() {
        return Math.max(getThreadPoolCoreSize(), 10);
    }

    default int getThreadPoolQueueSize() {
        return 10000;
    }


}
