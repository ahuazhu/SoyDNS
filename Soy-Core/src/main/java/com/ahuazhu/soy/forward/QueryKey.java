package com.ahuazhu.soy.forward;

import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public class QueryKey {

    private String key;

    public static QueryKey of(Message message) {
        QueryKey queryKey = new QueryKey();
        queryKey.key = message.getHeader().getID() + "_"
                + message.getQuestion().getName().toString() + "_"
                + message.getQuestion().getType();

        return queryKey;
    }

    @Override
    public int hashCode() {
        return key == null ? 0 : key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof QueryKey) {
            QueryKey o = (QueryKey) obj;
            return key == null ? o.key == null : key.equals(o.key);
        }
        return false;
    }
}
