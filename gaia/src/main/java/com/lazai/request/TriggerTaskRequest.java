package com.lazai.request;

import com.lazai.core.common.BasicRequest;

public class TriggerTaskRequest extends BasicRequest {

    private String taskNo;

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }
}
