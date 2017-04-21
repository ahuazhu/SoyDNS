package com.ahuazhu.soy.forward;

import java.util.List;

/**
 * Created by zhuzhengwen on 2017/4/21.
 */
public interface UpstreamManager {

    void init();

    List<TcpUpstream> getTcpUpstream();

    List<UdpUpstream> getUdpUpstream();

    void patrol();

}
