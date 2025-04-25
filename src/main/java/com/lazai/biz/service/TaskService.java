package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.entity.vo.TaskRecordVO;
import com.lazai.entity.vo.TaskTemplateVO;
import com.lazai.request.TaskCreateRequest;
import com.lazai.request.TaskQueryRequest;
import com.lazai.request.TriggerTaskRequest;

import java.util.List;

/**
 * Task biz service
 */
public interface TaskService {

    /**
     * create Task
     * @param request
     * @return
     */
    String createTask(TaskCreateRequest request);

    /**
     * trigger Task
     * @param request
     * @return
     */
    JSONObject triggerTask(TriggerTaskRequest request);

    /**
     * get User task records
     * @param taskQueryRequest
     * @return
     */
    List<TaskRecordVO> userTaskRecords(TaskQueryRequest taskQueryRequest);

    /**
     * create and trigger task
     * @param request
     */
    void createAndTriggerTask(TaskCreateRequest request);

    /**
     * claim task
     * @param request
     */
    void claimTask(TaskCreateRequest request);

    /**
     * get task template use situation
     * @param taskQueryRequest
     * @return
     */
    List<TaskTemplateVO> userTaskTemplatesUse(TaskQueryRequest taskQueryRequest);
}
