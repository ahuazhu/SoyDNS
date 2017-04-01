package com.ahuazhu.soy.executor;

import com.ahuazhu.soy.Soy;
import com.ahuazhu.soy.modal.Query;
import com.ahuazhu.soy.udp.ExecutorUtils;

import java.util.concurrent.ExecutorService;

/**
 * Created by zhengwenzhu on 2017/3/31.
 */
public enum Executors implements Executor {

    SyncExecutor {
        @Override
        public void execute(Query query) {
            Soy.fire(query);
        }
    },

    ThreadPoolExecutor {
        private final ExecutorService es = ExecutorUtils.newBlockingExecutors(1);

        @Override
        public void execute(Query query) {
            es.execute(() -> Soy.fire(query));
        }
    },
    Akka {
        @Override
        public void execute(Query query) {

        }
    },

    Quasar {
        @Override
        public void execute(Query query) {

        }
    }

}
