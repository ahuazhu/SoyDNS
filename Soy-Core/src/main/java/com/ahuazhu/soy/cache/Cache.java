package com.ahuazhu.soy.cache;

/**
 * Created by zhengwenzhu on 2017/4/4.
 */
public interface Cache<K, V> {

    V getValue(K key);

    void remove(K key);

    void putValue(K key, V value);

    default V takeValue(K key)  {
        V v = getValue(key);
        if (v != null) {
            remove(key);
        }
        return v;
    }

}
