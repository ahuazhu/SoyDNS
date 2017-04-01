package com.ahuazhu.soy.udp;

import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengwenzhu on 2017/3/30.
 */
public class ExecutorUtils {

    private static Logger LOGGER = Logger.getLogger(ExecutorUtils.class);

    public static ExecutorService newBlockingExecutors(int size) {

        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10000) {
                    @Override
                    public boolean offer(Runnable e) {
                        try {
                            this.put(e);
                        } catch (Exception e1) {
                            LOGGER.warn("offer error ", e1);
                        }
                        return true;
                    }
                });
    }
}