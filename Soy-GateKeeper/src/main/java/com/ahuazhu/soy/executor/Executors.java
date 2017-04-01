package com.ahuazhu.soy.executor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.ahuazhu.soy.Soy;
import com.ahuazhu.soy.modal.Query;
import com.ahuazhu.soy.udp.ExecutorUtils;
import com.ahuazhu.soy.utils.RecordBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
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
        ActorSystem system = ActorSystem.create("actor-demo-java");
        ActorRef soyActor = system.actorOf(Props.create(SoyActor.class));

        @Override
        public void execute(Query query) {
            soyActor.tell(query, soyActor);

        }

        class SoyActor extends AbstractActor {

            @Override
            public Receive createReceive() {
                return ReceiveBuilder.create().match(Query.class, Soy::fire).build();
            }
        }
    },

    Quasar {
        @Override
        public void execute(Query query) {

        }
    }

}
