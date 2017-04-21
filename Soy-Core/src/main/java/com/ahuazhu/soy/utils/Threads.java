package com.ahuazhu.soy.utils;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public class Threads {

    public static ThreadBuilder name(String name) {
        ThreadBuilder builder = new ThreadBuilder();
        builder.setName(name);
        return builder;
    }


    public static class ThreadBuilder {

        private Runnable target;

        private String name;

        private boolean isDaemon = false;


        public ThreadBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ThreadBuilder daemon(Runnable target) {
            this.target = target;
            this.isDaemon = true;
            return this;
        }

        public void start() {
            Thread thread = new Thread(target);
            thread.setName(name);
            thread.setDaemon(isDaemon);
            thread.start();
        }
    }

}
