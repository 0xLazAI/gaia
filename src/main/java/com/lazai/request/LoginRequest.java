package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class LoginRequest extends UserCreateRequest {


    private Boolean isForce;

    private String signature;

    private String invitedCode;

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getInvitedCode() {
        return invitedCode;
    }

    public void setInvitedCode(String invitedCode) {
        this.invitedCode = invitedCode;
    }

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
