package com.lazai.mapper;

import com.lazai.entity.TaskTemplate;
import com.lazai.entity.dto.TaskTemplateQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskTemplateMapper {

    Integer insert(TaskTemplate taskTemplate);

    Integer updateByCode(TaskTemplate taskTemplate);

    @Select("SELECT * FROM task_template WHERE template_code = #{templateCode}")
    TaskTemplate selectByCode(String templateCode);

    @Select("SELECT * FROM task_template WHERE status = #{status}")
    List<TaskTemplate> selectByStatus(String status);

    Integer queryListCnt(TaskTemplateQueryParam taskTemplateQueryParam);

    List<TaskTemplate> pageQueryList(TaskTemplateQueryParam taskTemplateQueryParam);

    List<TaskTemplate> queryList(TaskTemplateQueryParam taskTemplateQueryParam);
}
