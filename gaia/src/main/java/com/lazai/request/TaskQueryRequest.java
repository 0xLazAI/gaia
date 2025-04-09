package com.lazai.request;

import com.lazai.core.common.BasicRequest;

import java.util.Date;
import java.util.List;

public class TaskQueryRequest extends BasicRequest {

    private String innerPlatformUserId;

    private String bizUserId;

    private List<String> status;

    private String appToken;

    private List<String> templateCodes;

    private Date createdStartAt;

    private Date createdEndAt;

    private List<String> taskNos;

    public List<String> getTaskNos() {
        return taskNos;
    }

    public void setTaskNos(List<String> taskNos) {
        this.taskNos = taskNos;
    }

    public Date getCreatedStartAt() {
        return createdStartAt;
    }

    public void setCreatedStartAt(Date createdStartAt) {
        this.createdStartAt = createdStartAt;
    }

    public Date getCreatedEndAt() {
        return createdEndAt;
    }

    public void setCreatedEndAt(Date createdEndAt) {
        this.createdEndAt = createdEndAt;
    }

    public String getInnerPlatformUserId() {
        return innerPlatformUserId;
    }

    public void setInnerPlatformUserId(String innerPlatformUserId) {
        this.innerPlatformUserId = innerPlatformUserId;
    }

    public String getBizUserId() {
        return bizUserId;
    }

    public void setBizUserId(String bizUserId) {
        this.bizUserId = bizUserId;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public List<String> getTemplateCodes() {
        return templateCodes;
    }

    public void setTemplateCodes(List<String> templateCodes) {
        this.templateCodes = templateCodes;
    }
}
