package com.ahuazhu.soy.modal;

import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/4/1.
 */
public class QueryKey {


    private int questionId;

    private String name;

    private int type;

    public QueryKey(Message message) {
        questionId = message.getHeader().getID();
        name = message.getQuestion().getName().toString();
        type = message.getQuestion().getType();
    }

    @Deprecated()
    public static QueryKey of(Message message) {
        return new QueryKey(message);
    }

    @Override
    public int hashCode() {
        return questionId << 7
                + type
                + (name == null ? 0 : name.toLowerCase().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof QueryKey) {
            QueryKey o = (QueryKey) obj;
            return questionId == o.questionId
                    && type == o.type
                    && (name == null ? o.name == null : name.equalsIgnoreCase(o.name));
        }
        return false;
    }
}
