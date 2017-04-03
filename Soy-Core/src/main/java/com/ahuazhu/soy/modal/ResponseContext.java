package com.ahuazhu.soy.modal;

import org.ehcache.Cache;
import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class ResponseContext {
    ResponseWriter writer;

    private Throwable error;

    private Message result;

    private Cache<QueryKey, Message> cache;

    public ResponseContext(ResponseWriter writer) {
        this.writer = writer;
    }

    public ResponseWriter getWriter() {
        return writer;
    }

    public boolean hasError() {
        return error != null;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Message getResult() {
        return result;
    }

    public void setResult(Message result) {
        this.result = result;
    }

    public void response(Message message) {
        result = message;
        cache.put(QueryKey.of(message), message);
    }

    public Cache<QueryKey, Message> getCache() {
        return cache;
    }

    public void setCache(Cache<QueryKey, Message> cache) {
        this.cache = cache;
    }
}
