package com.lazai.repostories.impl;

import com.lazai.entity.TaskTemplate;
import com.lazai.entity.dto.CommonPageDTO;
import com.lazai.entity.dto.CommonPageResult;
import com.lazai.entity.dto.TaskTemplateQueryParam;
import com.lazai.enums.TaskTemplateStatus;
import com.lazai.mapper.TaskTemplateMapper;
import com.lazai.repostories.TaskTemplateRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskTemplateRepositoryImpl implements TaskTemplateRepository {

    @Autowired
    private TaskTemplateMapper taskTemplateMapper;

    public String insert(TaskTemplate taskTemplate){
        if (taskTemplate.getCreatedAt() == null) {
            taskTemplate.setCreatedAt(new Date());
        }
        if (taskTemplate.getUpdatedAt() == null) {
            taskTemplate.setUpdatedAt(new Date());
        }
        if (taskTemplate.getApp() == null) {
            taskTemplate.setApp("gaia");
        }
        if(StringUtils.isEmpty(taskTemplate.getStatus())){
            taskTemplate.setStatus(TaskTemplateStatus.ACTIVE.value());
        }
        if (StringUtils.isEmpty(taskTemplate.getOperator())) {
            taskTemplate.setOperator(taskTemplate.getCreator());
        }
        taskTemplateMapper.insert(taskTemplate);
        return taskTemplate.getTemplateCode();
    }

    public Integer updateByCode(TaskTemplate taskTemplate){
        taskTemplate.setUpdatedAt(new Date());
        return taskTemplateMapper.updateByCode(taskTemplate);
    }

    public List<TaskTemplate> selectByStatus(String status){

        return taskTemplateMapper.selectByStatus(status);
    }

    public TaskTemplate selectByCode(String templateCode){

        return taskTemplateMapper.selectByCode(templateCode);
    }

    public CommonPageResult<TaskTemplate> pageQueryList(TaskTemplateQueryParam taskTemplateQueryParam){
        Integer cnt = queryListCnt(taskTemplateQueryParam);

        List<TaskTemplate> items = taskTemplateMapper.pageQueryList(taskTemplateQueryParam);
        CommonPageResult result = new CommonPageResult<TaskTemplate>();
        CommonPageDTO pageDTO = new CommonPageDTO();
        pageDTO.setPageSize(taskTemplateQueryParam.getPageSize());
        pageDTO.setTotalCount(cnt);
        result.setPagination(pageDTO);
        result.setItems(items);
        return result;
    }

    public List<TaskTemplate> queryList(TaskTemplateQueryParam taskTemplateQueryParam){
        return taskTemplateMapper.queryList(taskTemplateQueryParam);
    }

    public Integer queryListCnt(TaskTemplateQueryParam taskTemplateQueryParam){
        return taskTemplateMapper.queryListCnt(taskTemplateQueryParam);
    }
}
