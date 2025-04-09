package com.lazai.mapper;

import com.lazai.entity.TaskAction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskActionMapper {

   Integer insert(TaskAction taskAction);

   Integer updateById(TaskAction taskAction);

    @Select("SELECT * FROM task_actions WHERE biz_no = #{bizNo} AND biz_type= #{bizType}")
    List<TaskAction> queryByBizNoAndBizType(String bizNo, String bizType);

}
