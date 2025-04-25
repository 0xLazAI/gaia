package com.lazai.biz.service;

import com.lazai.entity.TaskTemplate;
import com.lazai.entity.dto.CommonPageResult;
import com.lazai.request.TaskTemplateCreateRequest;
import com.lazai.request.TaskTemplateListQueryRequest;

import java.util.List;

/**
 * TaskTemplate biz service
 */
public interface TaskTemplateService {

    /**
     * create task template
     * @param taskTemplateCreateRequest
     */
    void createTaskTemplate(TaskTemplateCreateRequest taskTemplateCreateRequest);

    /**
     * get task template list
     * @param request
     * @return
     */
    List<TaskTemplate> taskTemplateList(TaskTemplateListQueryRequest request);

    /**
     * update task template
     * @param request
     */
    void updateByCode(TaskTemplateCreateRequest request);

    /**
     * find template by code
     * @param templateCode
     * @return
     */
    TaskTemplate selectByCode(String templateCode);

    /**
     * page query template task
     * @param taskTemplateQueryParam
     * @return
     */
    CommonPageResult<TaskTemplate> pageQueryList(TaskTemplateListQueryRequest taskTemplateQueryParam);

}
