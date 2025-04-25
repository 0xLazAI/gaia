package com.lazai.repostories.impl;

import com.lazai.entity.TaskRecord;
import com.lazai.entity.dto.TaskRecordQueryParam;
import com.lazai.enums.TaskStatusEnum;
import com.lazai.enums.TaskTemplateStatus;
import com.lazai.mapper.TaskRecordMapper;
import com.lazai.repostories.TaskRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskRecordRepositoryImpl implements TaskRecordRepository {

    @Autowired
    private TaskRecordMapper taskRecordMapper;

    public Integer insert(TaskRecord taskRecord){
        if (taskRecord.getCreatedAt() == null) {
            taskRecord.setCreatedAt(new Date());
        }
        if (taskRecord.getUpdatedAt() == null) {
            taskRecord.setUpdatedAt(new Date());
        }
        if(StringUtils.isEmpty(taskRecord.getStatus())){
            taskRecord.setStatus(TaskStatusEnum.INIT.value());
        }
        if (StringUtils.isEmpty(taskRecord.getOperator())) {
            taskRecord.setOperator(taskRecord.getCreator());
        }
        return taskRecordMapper.insert(taskRecord);
    }

    public Integer updateByTaskNo(TaskRecord taskRecord){
        taskRecord.setUpdatedAt(new Date());
        return taskRecordMapper.updateByTaskNo(taskRecord);
    }

    public TaskRecord selectByTaskNo(String taskNo, Boolean isLock){
        if(isLock){
            return taskRecordMapper.selectByTaskNoLock(taskNo);
        }
        return taskRecordMapper.selectByTaskNo(taskNo);
    }

    public Integer queryListCnt(TaskRecordQueryParam taskRecordQueryParam){
        return taskRecordMapper.queryListCnt(taskRecordQueryParam);
    }

    public List<TaskRecord> pageQueryList(TaskRecordQueryParam taskRecordQueryParam){
        return taskRecordMapper.pageQueryList(taskRecordQueryParam);
    }

    public List<TaskRecord> queryList(TaskRecordQueryParam taskRecordQueryParam){
        return taskRecordMapper.queryList(taskRecordQueryParam);
    }

}
