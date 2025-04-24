package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.TaskService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.core.common.RoleDTO;
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
        User loginUser = JWTUtils.getUser();
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        taskCreateRequest.setOperator(operator);
        taskCreateRequest.setInnerUserId(loginUser.getId()+"");
        return JsonApiResponse.success(taskService.createTask(taskCreateRequest));
    }

    @PostMapping("/triggerTask")
    @ResultLog(name = "TaskController.triggerTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> triggerTask(@RequestBody TriggerTaskRequest triggerTaskRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        triggerTaskRequest.setOperator(operator);
        return JsonApiResponse.success(taskService.triggerTask(triggerTaskRequest));
    }

    @PostMapping("/createAndTriggerTask")
    @ResultLog(name = "TaskController.createAndTriggerTask", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createAndTriggerTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        taskCreateRequest.setInnerUserId(loginUser.getId()+"");
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        taskCreateRequest.setOperator(operator);
        taskService.createAndTriggerTask(taskCreateRequest);
        return JsonApiResponse.success(true);
    }

    @PostMapping("/claim")
    @ResultLog(name = "TaskController.claim", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> claim(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        taskCreateRequest.setInnerUserId(loginUser.getId()+"");
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        taskCreateRequest.setOperator(operator);
        taskService.claimTask(taskCreateRequest);
        return JsonApiResponse.success(true);
    }

    @GetMapping("/userTaskRecords")
    @ResultLog(name = "TestController.userTaskRecords", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> userTaskRecords(@ModelAttribute TaskQueryRequest bizRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        bizRequest.setInnerPlatformUserId(loginUser.getId()+"");
        return JsonApiResponse.success(taskService.userTaskRecords(bizRequest));
    }

    @GetMapping("/userTaskTemplatesUse")
    @ResultLog(name = "TestController.userTaskTemplatesUse", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> userTaskTemplatesUse(@ModelAttribute TaskQueryRequest bizRequest, HttpServletRequest request){
        User loginUser = JWTUtils.getUser();
        bizRequest.setInnerPlatformUserId(loginUser.getId()+"");
        return JsonApiResponse.success(taskService.userTaskTemplatesUse(bizRequest));
    }
}
