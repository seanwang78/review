package com.payby.gateway.openapi;

import java.io.Serializable;

import lombok.Data;

@Data
public class SgsRequestWrap<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7480161729401607324L;

    /** request time, Each time execute need difference */
    private Long requestTime = System.currentTimeMillis();

    private T bizContent;

    public static <T> SgsRequestWrap<T> wrap(T t) {
        SgsRequestWrap wrap = new SgsRequestWrap();
        wrap.setBizContent(t);
        return wrap;
    }

    public static <T> SgsRequestWrap<T> wrap() {
        SgsRequestWrap wrap = new SgsRequestWrap();
        return wrap;
    }
}