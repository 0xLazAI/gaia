package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.TaskTemplateService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.TaskTemplateCreateRequest;
import com.lazai.request.TaskTemplateListQueryRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/task")
public class TaskManageController {

    @Autowired
    private TaskTemplateService taskTemplateService;


    @GetMapping("/taskTemplateList")
    @ResultLog(name = "TaskManageController.taskTemplateList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> taskTemplateList(@ModelAttribute TaskTemplateListQueryRequest taskTemplateListQueryRequest, HttpServletRequest request){
        return JsonApiResponse.success(taskTemplateService.taskTemplateList(taskTemplateListQueryRequest));
    }

    @PostMapping("/createTaskTemplate")
    @ResultLog(name = "TaskManageController.createTaskTemplate", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createTaskTemplate(@RequestBody TaskTemplateCreateRequest bizRequest, HttpServletRequest request){
        taskTemplateService.createTaskTemplate(bizRequest);
        return JsonApiResponse.success(true);
    }

    @PostMapping("/updateByCode")
    @ResultLog(name = "TaskManageController.updateByCode", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> updateByCode(@RequestBody TaskTemplateCreateRequest bizRequest, HttpServletRequest request){
        taskTemplateService.updateByCode(bizRequest);
        return JsonApiResponse.success(true);
    }

    @GetMapping("/selectByCode")
    @ResultLog(name = "TaskManageController.selectByCode", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> selectByCode(@RequestParam String templateCode, HttpServletRequest request){
        return JsonApiResponse.success(taskTemplateService.selectByCode(templateCode));
    }

    @GetMapping("/pageQueryList")
    @ResultLog(name = "TaskManageController.pageQueryList", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> pageQueryList(@ModelAttribute TaskTemplateListQueryRequest taskTemplateListQueryRequest, HttpServletRequest request){
        return JsonApiResponse.success(taskTemplateService.pageQueryList(taskTemplateListQueryRequest));
    }

}
