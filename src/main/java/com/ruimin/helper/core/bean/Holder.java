package com.ruimin.helper.core.bean;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/02/19 下午 07:50
 * @description
 */

public class Holder<T> {

    public Holder(T value) {
        this.value = value;
    }

    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
