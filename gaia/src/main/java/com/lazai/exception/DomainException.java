package com.lazai.exception;

public class DomainException extends RuntimeException{

    private Integer code = 500;

    public DomainException(String message, Integer code){
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
