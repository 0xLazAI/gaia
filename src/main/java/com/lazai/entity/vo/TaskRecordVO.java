package com.lazai.entity.vo;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskRecordVO implements Serializable {


    private static final long serialVersionUID = 2896202029079986129L;


    private BigInteger id;

    private String taskNo;

    private String taskName;

    private String taskTemplateId;

    private String content;

    private String context;

    private String creator;

    private String operator;

    private Integer version;

    private String status;

    private String createdAt;

    private String updatedAt;

    private String innerUser;

    private String outerUser;

    private String app;

    private List<JSONObject> scoreInfo = new ArrayList<>();

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<JSONObject> getScoreInfo() {
        return scoreInfo;
    }

    public void setScoreInfo(List<JSONObject> scoreInfo) {
        this.scoreInfo = scoreInfo;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getTaskTemplateId() {
        return taskTemplateId;
    }

    public void setTaskTemplateId(String taskTemplateId) {
        this.taskTemplateId = taskTemplateId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(String innerUser) {
        this.innerUser = innerUser;
    }

    public String getOuterUser() {
        return outerUser;
    }

    public void setOuterUser(String outerUser) {
        this.outerUser = outerUser;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
