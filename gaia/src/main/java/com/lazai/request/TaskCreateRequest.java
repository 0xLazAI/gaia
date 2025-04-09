package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class TaskCreateRequest extends BasicRequest {

    private String templateCode;

    private String bizType;

    private String bizId;

    private String app;

    private String outerUserId;

    private String innerUserId;

    public String getInnerUserId() {
        return innerUserId;
    }

    public void setInnerUserId(String innerUserId) {
        this.innerUserId = innerUserId;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getOuterUserId() {
        return outerUserId;
    }

    public void setOuterUserId(String outerUserId) {
        this.outerUserId = outerUserId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
