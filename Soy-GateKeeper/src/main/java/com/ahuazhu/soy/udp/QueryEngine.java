package com.ahuazhu.soy.udp;

import com.ahuazhu.soy.Soy;
import com.ahuazhu.soy.modal.Request;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Created by zhengwenzhu on 2017/3/31.
 */
public class QueryEngine {

    private Semaphore emptyNumber;

    private Semaphore taskNumber;

    private Queue queue;

    public QueryEngine(int workerNumber, int queueSize) {
        emptyNumber = new Semaphore(queueSize);
        taskNumber = new Semaphore(0);
        queue = new Queue();

        for (int i = 0; i < workerNumber; i++) {
            Thread t = new Thread(new Worker());
            t.setDaemon(true);
            t.start();
        }
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            for (; ; ) {
                try {
                    taskNumber.acquire();
                    Request request = queue.poll();
                    emptyNumber.release();

                    process(request);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void process(Request request) {
            Soy.fire(request);
        }

    }

    public void submit(Request request) {
        queue.offer(request);
    }


    private class Queue extends LinkedBlockingQueue<Request> {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public boolean offer(Request r) {
            try {
                emptyNumber.acquire();
                super.offer(r);
                taskNumber.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

}
