package com.lazai.entity.vo;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class TaskTemplateVO implements Serializable {


    private static final long serialVersionUID = -4701370487660212035L;

    private String taskName;

    private String desc;

    private String taskTemplateId;

    private Integer taskFinishCount;

    private Integer taskCount;

    private String taskType;

    private JSONObject content;

    private JSONObject context;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public JSONObject getContext() {
        return context;
    }

    public void setContext(JSONObject context) {
        this.context = context;
    }

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
