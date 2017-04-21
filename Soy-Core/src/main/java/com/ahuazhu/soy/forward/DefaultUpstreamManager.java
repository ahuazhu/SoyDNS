package com.ahuazhu.soy.forward;

import com.ahuazhu.soy.config.Configure;

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
        Thread threadForReceive = new Thread(this::patrol, "Upstream-Patrol");
        threadForReceive.setDaemon(true);
        threadForReceive.start();
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
