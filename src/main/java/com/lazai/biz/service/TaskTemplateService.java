package com.lazai.biz.service;

import com.lazai.entity.TaskTemplate;
import com.lazai.entity.dto.CommonPageResult;
import com.lazai.request.TaskTemplateCreateRequest;
import com.lazai.request.TaskTemplateListQueryRequest;

import java.util.List;

public interface TaskTemplateService {

    void createTaskTemplate(TaskTemplateCreateRequest taskTemplateCreateRequest);

    List<TaskTemplate> taskTemplateList(TaskTemplateListQueryRequest request);

    void updateByCode(TaskTemplateCreateRequest request);

    TaskTemplate selectByCode(String templateCode);

    CommonPageResult<TaskTemplate> pageQueryList(TaskTemplateListQueryRequest taskTemplateQueryParam);

}
