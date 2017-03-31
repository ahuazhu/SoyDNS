package com.ahuazhu.soy.udp;

import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * Created by zhengwenzhu on 2017/3/30.
 */
public class ExecutorUtils {

    private static Logger LOGGER = Logger.getLogger(ExecutorUtils.class);

    /**
     * @param size
     * @return
     */
    public static ExecutorService newBlockingExecutors(int size) {

//        return Executors.newSingleThreadExecutor();
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10000) {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    /**
                     * (non-Jsdoc)
                     *
                     * @see java.util.concurrent.LinkedBlockingQueue#offer(java.lang.Object)
                     */
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