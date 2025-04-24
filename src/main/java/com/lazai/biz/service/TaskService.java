package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.entity.vo.TaskRecordVO;
import com.lazai.entity.vo.TaskTemplateVO;
import com.lazai.request.TaskCreateRequest;
import com.lazai.request.TaskQueryRequest;
import com.lazai.request.TriggerTaskRequest;

import java.util.List;

public interface TaskService {

    String createTask(TaskCreateRequest request);

    JSONObject triggerTask(TriggerTaskRequest request);

    List<TaskRecordVO> userTaskRecords(TaskQueryRequest taskQueryRequest);

    void createAndTriggerTask(TaskCreateRequest request);

    void claimTask(TaskCreateRequest request);

    List<TaskTemplateVO> userTaskTemplatesUse(TaskQueryRequest taskQueryRequest);
}
