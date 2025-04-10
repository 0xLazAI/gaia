package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class BindTwitterUserInfoRequest extends BasicRequest {


    private static final long serialVersionUID = 5115740632268538290L;

    private String code;

    private String status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
