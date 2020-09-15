package com.xc.framework.bean;

import java.io.Serializable;

/**
 * Date：2020/9/14
 * Author：ZhangXuanChen
 * Description：键值对bean
 */
public class KVBean<K, V> implements Serializable {
    private K key;
    private V value;

    public KVBean() {
    }

    public KVBean(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KVBean{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
