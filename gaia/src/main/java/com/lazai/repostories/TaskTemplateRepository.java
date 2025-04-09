package com.lazai.repostories;

import com.lazai.entity.TaskTemplate;
import com.lazai.entity.dto.CommonPageResult;
import com.lazai.entity.dto.TaskTemplateQueryParam;

import java.util.List;

public interface TaskTemplateRepository {

    String insert(TaskTemplate taskTemplate);

    Integer updateByCode(TaskTemplate taskTemplate);

    List<TaskTemplate> selectByStatus(String status);

    TaskTemplate selectByCode(String templateCode);

    CommonPageResult<TaskTemplate> pageQueryList(TaskTemplateQueryParam taskTemplateQueryParam);

    List<TaskTemplate> queryList(TaskTemplateQueryParam taskTemplateQueryParam);

    Integer queryListCnt(TaskTemplateQueryParam taskTemplateQueryParam);
}
