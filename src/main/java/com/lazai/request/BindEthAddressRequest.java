package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class BindEthAddressRequest extends BasicRequest {


    private static final long serialVersionUID = 310249798524537362L;

    private String userId;

    private String ethAddress;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }
}
