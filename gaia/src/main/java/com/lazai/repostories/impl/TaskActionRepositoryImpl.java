package com.lazai.repostories.impl;

import com.lazai.entity.TaskAction;
import com.lazai.mapper.TaskActionMapper;
import com.lazai.repostories.TaskActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskActionRepositoryImpl implements TaskActionRepository {

    @Autowired
    private TaskActionMapper taskActionMapper;

    public Integer insert(TaskAction taskAction){
        if (taskAction.getCreatedAt() == null) {
            taskAction.setCreatedAt(new Date());
        }
        if (taskAction.getUpdatedAt() == null) {
            taskAction.setUpdatedAt(new Date());
        }
        return taskActionMapper.insert(taskAction);
    }

    public Integer updateById(TaskAction taskAction){
        taskAction.setUpdatedAt(new Date());
        return taskActionMapper.updateById(taskAction);
    }

    public List<TaskAction> queryByBizNoAndBizType(String bizNo, String bizType){
        return taskActionMapper.queryByBizNoAndBizType(bizNo, bizType);
    }
}
