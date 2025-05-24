package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class BindInvitingCodeRequest extends BasicRequest {

    private static final long serialVersionUID = -5297100569710630343L;

    private String userId;

    private String invitedCode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInvitedCode() {
        return invitedCode;
    }

    public void setInvitedCode(String invitedCode) {
        this.invitedCode = invitedCode;
    }
}
