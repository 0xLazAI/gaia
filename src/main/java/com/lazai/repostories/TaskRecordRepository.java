package com.lazai.repostories;

import com.lazai.entity.TaskRecord;
import com.lazai.entity.dto.TaskRecordQueryParam;

import java.util.List;

public interface TaskRecordRepository {

    Integer insert(TaskRecord taskRecord);

    Integer updateByTaskNo(TaskRecord taskRecord);

    TaskRecord selectByTaskNo(String taskNo, Boolean isLock);

    Integer queryListCnt(TaskRecordQueryParam taskRecordQueryParam);

    List<TaskRecord> pageQueryList(TaskRecordQueryParam taskRecordQueryParam);

    List<TaskRecord> queryList(TaskRecordQueryParam taskRecordQueryParam);
}
