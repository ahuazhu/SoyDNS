package com.ahuazhu.soy.cache;

/**
 * Created by zhengwenzhu on 2017/4/4.
 */
public interface Cache<K, V> {

    V getValue(K key);

    void remove(K key);

    void putValue(K key, V value);

}
