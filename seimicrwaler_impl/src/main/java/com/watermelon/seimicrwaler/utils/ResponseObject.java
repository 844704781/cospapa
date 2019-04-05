package com.watermelon.seimicrwaler.utils;

import lombok.Data;

/**
 * Created by watermelon on 2019/04/05
 */
@Data
public class ResponseObject<T> {

    private Integer code;
    private String message;
    private T object;

    public ResponseObject(Integer code) {
        this.code = code;
    }

    public ResponseObject(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
