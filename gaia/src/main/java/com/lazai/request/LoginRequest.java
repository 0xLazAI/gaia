package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class LoginRequest extends UserCreateRequest {


    private Boolean isForce;

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean getForce() {
        return isForce;
    }

    public void setForce(Boolean force) {
        isForce = force;
    }
}
