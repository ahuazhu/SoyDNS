package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.config.Configure;
import com.ahuazhu.soy.utils.Threads;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public class DefaultUpstreamManager implements UpstreamManager {

    private List<TcpUpstream> tcpUpstreams;

    private List<UdpUpstream> udpUpstreams;


    private Configure configure;

    public DefaultUpstreamManager(Configure configure) {
        this.configure = configure;
    }

    @Override
    public void init() {
        Threads.name("UPSTREAM-PATROL").daemon(this::patrol).start();
    }

    @Override
    public Collection<TcpUpstream> getTcpUpstream() {

        return Collections.unmodifiableCollection(tcpUpstreams);
    }

    @Override
    public Collection<UdpUpstream> getUdpUpstream() {

        return Collections.unmodifiableCollection(udpUpstreams);
    }

    @Override
    public void patrol() {
        for (; ; ) {
            tcpUpstreams.stream().filter(Upstream::lost).forEach(Upstream::reconnect);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //
            }

        }
    }
}
