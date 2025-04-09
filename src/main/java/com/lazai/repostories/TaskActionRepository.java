package com.lazai.repostories;

import com.lazai.entity.TaskAction;

import java.util.List;

public interface TaskActionRepository {
    Integer insert(TaskAction taskAction);

    Integer updateById(TaskAction taskAction);

    List<TaskAction> queryByBizNoAndBizType(String bizNo, String bizType);
}
