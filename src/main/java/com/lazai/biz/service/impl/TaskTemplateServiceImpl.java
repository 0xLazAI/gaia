package com.lazai.biz.service.impl;

import com.lazai.biz.service.TaskTemplateService;
import com.lazai.entity.TaskTemplate;
import com.lazai.entity.dto.CommonPageResult;
import com.lazai.entity.dto.TaskTemplateQueryParam;
import com.lazai.repostories.TaskTemplateRepository;
import com.lazai.request.TaskTemplateCreateRequest;
import com.lazai.request.TaskTemplateListQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @see TaskTemplateService
 */
@Service
public class TaskTemplateServiceImpl implements TaskTemplateService {

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    /**
     * @see TaskTemplateService#createTaskTemplate
     */
    @Override
    public void createTaskTemplate(TaskTemplateCreateRequest taskTemplateCreateRequest){
        taskTemplateRepository.insert(convertTaskTemplateCreateRequestToTaskTemplate(taskTemplateCreateRequest));
    }

    /**
     * @see TaskTemplateService#taskTemplateList
     */
    @Override
    public List<TaskTemplate> taskTemplateList(TaskTemplateListQueryRequest request){
        return taskTemplateRepository.queryList(convertToTaskTemplateQueryParam(request));
    }

    /**
     * @see TaskTemplateService#updateByCode
     */
    @Override
    public void updateByCode(TaskTemplateCreateRequest request){
        taskTemplateRepository.updateByCode(convertTaskTemplateCreateRequestToTaskTemplate(request));
    }

    /**
     * @see TaskTemplateService#selectByCode
     */
    @Override
    public TaskTemplate selectByCode(String templateCode){
        return taskTemplateRepository.selectByCode(templateCode);
    }

    /**
     * @see TaskTemplateService#pageQueryList
     */
    @Override
    public CommonPageResult<TaskTemplate> pageQueryList(TaskTemplateListQueryRequest request){
        return taskTemplateRepository.pageQueryList(convertToTaskTemplateQueryParam(request));
    }

    /**
     * convert to TaskTemplate
     * @param request
     * @return
     */
    public static TaskTemplate convertTaskTemplateCreateRequestToTaskTemplate(TaskTemplateCreateRequest request){
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setTemplateCode(request.getTemplateCode());
        taskTemplate.setTemplateName(request.getTemplateName());
        taskTemplate.setCreator(request.getOperator().getId());
        taskTemplate.setOperator(request.getOperator().getId());
        taskTemplate.setApp(request.getApp());
        taskTemplate.setContent(request.getContent().toJSONString());
        return taskTemplate;
    }

    /**
     * convert to TaskTemplateQueryParam
     * @param request
     * @return
     */
    public static TaskTemplateQueryParam convertToTaskTemplateQueryParam(TaskTemplateListQueryRequest request){
        TaskTemplateQueryParam taskTemplateQueryParam = new TaskTemplateQueryParam();
        taskTemplateQueryParam.setStatus(request.getStatus());
        taskTemplateQueryParam.setApp(request.getApp());
        taskTemplateQueryParam.setTemplateCode(request.getTemplateCode());
        taskTemplateQueryParam.setTemplateName(request.getTemplateName());
        taskTemplateQueryParam.setCreator(request.getCreator());
        Integer offset = (request.getPageNum()-1) * request.getPageSize();
        taskTemplateQueryParam.setOffset(offset);
        taskTemplateQueryParam.setPageSize(request.getPageSize());
        return taskTemplateQueryParam;

    }



}
