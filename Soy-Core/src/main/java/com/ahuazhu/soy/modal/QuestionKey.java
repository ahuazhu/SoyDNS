package com.ahuazhu.soy.modal;

import org.xbill.DNS.Message;

/**
 * Created by zhengwenzhu on 2017/4/5.
 */
public class QuestionKey {

    private String name;

    private int type;

    private int questionId;

    public QuestionKey(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message should not be null");
        }

        this.name = message.getQuestion().getName().toString();
        this.type = message.getQuestion().getType();
        this.questionId = message.getHeader().getID();
    }

    public int getQuestionId() {
        return questionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof QuestionKey) {
            QuestionKey o = (QuestionKey) obj;
            return type == o.type && (name == null ? o.name == null : name.equalsIgnoreCase(o.name));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return type + (name == null ? 0 : name.hashCode());
    }
}
