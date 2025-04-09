package com.lazai.mapper;

import com.lazai.entity.TaskRecord;
import com.lazai.entity.dto.TaskRecordQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskRecordMapper {

    Integer insert(TaskRecord taskRecord);

    Integer updateByTaskNo(TaskRecord taskRecord);

    @Select("SELECT * FROM task_record WHERE task_no = #{taskNo}")
    TaskRecord selectByTaskNo(String taskNo);

    @Select("SELECT * FROM task_record WHERE task_no = #{taskNo} FOR UPDATE")
    TaskRecord selectByTaskNoLock(String taskNo);

    Integer queryListCnt(TaskRecordQueryParam taskRecordQueryParam);

    List<TaskRecord> pageQueryList(TaskRecordQueryParam taskRecordQueryParam);

    List<TaskRecord> queryList(TaskRecordQueryParam taskRecordQueryParam);

}
