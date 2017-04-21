package com.ahuazhu.soy.forward;

import java.util.Collection;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public interface UpstreamManager {

    void init();

    Collection<TcpUpstream> getTcpUpstream();

    Collection<UdpUpstream> getUdpUpstream();

    void patrol();

}
