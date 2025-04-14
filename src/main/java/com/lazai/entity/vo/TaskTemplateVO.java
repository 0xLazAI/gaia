package com.lazai.entity.vo;

import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;

public class TaskTemplateVO {

    private String taskName;

    private String taskTemplateId;

    private Integer taskFinishCount;

    private Integer taskCount;

    private String taskType;

    private JSONObject content;

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskTemplateId() {
        return taskTemplateId;
    }

    public void setTaskTemplateId(String taskTemplateId) {
        this.taskTemplateId = taskTemplateId;
    }

    public Integer getTaskFinishCount() {
        return taskFinishCount;
    }

    public void setTaskFinishCount(Integer taskFinishCount) {
        this.taskFinishCount = taskFinishCount;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }
}
