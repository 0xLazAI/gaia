package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.TaskService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.entity.User;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.TaskCreateRequest;
import com.lazai.request.TaskQueryRequest;
import com.lazai.request.TriggerTaskRequest;
import com.lazai.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/createTask")
    @ResultLog(name = "TaskController.createTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        User user = JWTUtils.getUser();
        return JsonApiResponse.success(taskService.createTask(taskCreateRequest));
    }

    @PostMapping("/triggerTask")
    @ResultLog(name = "TaskController.triggerTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> triggerTask(@RequestBody TriggerTaskRequest triggerTaskRequest, HttpServletRequest request){
        return JsonApiResponse.success(taskService.triggerTask(triggerTaskRequest));
    }

    @PostMapping("/createAndTriggerTask")
    @ResultLog(name = "TaskController.createAndTriggerTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createAndTriggerTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        taskService.createAndTriggerTask(taskCreateRequest);
        return JsonApiResponse.success(true);
    }

    @GetMapping("/userTaskRecords")
    @ResultLog(name = "TestController.userTaskRecords", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> userTaskRecords(@ModelAttribute TaskQueryRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(taskService.userTaskRecords(bizRequest));
    }
}
