package com.ahuazhu.soy.executor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.ahuazhu.soy.Soy;
import com.ahuazhu.soy.model.Query;
import com.lmax.disruptor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengwenzhu on 2017/3/31.
 */
public enum Executors implements Executor {
    Sync {
        @Override
        public void execute(Query query) {
            Soy.fire(query);
        }
    },

    ThreadPool {
        private final ExecutorService es = createBlockingExecutors(10);

        @Override
        public void execute(Query query) {
            es.execute(() -> Soy.fire(query));
        }


    },

    Akka {
        private ActorSystem system = ActorSystem.create("Soy-Actor");
        private ActorRef soyActor = system.actorOf(Props.create(SoyActor.class));

        @Override
        public void execute(Query query) {
            soyActor.tell(query, ActorRef.noSender());
        }

    },

    Disruptor {
        RingBuffer<DisruptorQuery> ringBuffer = null;

        {
            final int BUFFER_SIZE = 1 << 10 << 4; // 1024 * 16
            final int THREAD_NUMBERS = 10;
            EventFactory<DisruptorQuery> eventFactory = DisruptorQuery::new;
            ringBuffer = RingBuffer.createSingleProducer(eventFactory, BUFFER_SIZE);
            SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
            ExecutorService executor = createBlockingExecutors(THREAD_NUMBERS);
            WorkHandler<DisruptorQuery> workHandlers = o -> Soy.fire(o.getQuery());
            WorkerPool<DisruptorQuery> workerPool = new WorkerPool<>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), workHandlers);
            workerPool.start(executor);
        }

        @Override
        public void execute(Query query) {
            long seq = ringBuffer.next();
            ringBuffer.get(seq).setQuery(query);
            ringBuffer.publish(seq);
        }
    };

    Logger logger = LoggerFactory.getLogger(Executors.class);

    public Executor of(String name) {

        Executor e = Executors.valueOf(name);
        if (e == null) {
            e = Sync;
            logger.warn("Executor of {} not found, use {} instead.", name, e.toString());
        }

        return e;
    }

    ExecutorService createBlockingExecutors(int size) {
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10000) {
                    @Override
                    public boolean offer(Runnable e) {
                        try {
                            this.put(e);
                        } catch (Exception e1) {
                            logger.error("", e1);
                        }
                        return true;
                    }
                });
    }
}

class SoyActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(Query.class, Soy::fire).build();
    }
}

class DisruptorQuery {
    Query query;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }
}