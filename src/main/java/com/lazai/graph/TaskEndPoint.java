package com.lazai.graph;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.TaskService;
import com.lazai.core.common.GraphQLResponse;
import com.lazai.core.common.RoleDTO;
import com.lazai.entity.User;
import com.lazai.entity.vo.TaskRecordVO;
import com.lazai.entity.vo.TaskTemplateVO;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.BindUserEthRequest;
import com.lazai.request.TaskCreateRequest;
import com.lazai.request.TaskQueryRequest;
import com.lazai.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TaskEndPoint {

    @Autowired
    private TaskService taskService;

    @ResultLog(name = "TaskEndPoint.userTaskTemplatesUse", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("getUserTaskTemplatesUse")
    public GraphQLResponse<List<TaskTemplateVO>> userTaskTemplatesUse(@Argument("bizReq") TaskQueryRequest taskQueryRequest){
        User loginUser = JWTUtils.getUser();
        taskQueryRequest.setInnerPlatformUserId(loginUser.getId()+"");
        return GraphQLResponse.success(taskService.userTaskTemplatesUse(taskQueryRequest));
    }

    @ResultLog(name = "TaskEndPoint.userTaskRecords", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("getUserTaskRecords")
    public GraphQLResponse<List<TaskRecordVO>> userTaskRecords(@Argument("bizReq") TaskQueryRequest taskQueryRequest){
        User loginUser = JWTUtils.getUser();
        taskQueryRequest.setInnerPlatformUserId(loginUser.getId()+"");
        return GraphQLResponse.success(taskService.userTaskRecords(taskQueryRequest));
    }


    @ResultLog(name = "UserEndPoint.createAndTriggerTask", methodType = MethodTypeEnum.UPPER)
    @MutationMapping("claim")
    public GraphQLResponse<Boolean> createAndTriggerTask(@Argument("req") TaskCreateRequest request){
        User loginUser = JWTUtils.getUser();
        request.setInnerUserId(loginUser.getId()+"");
        RoleDTO operator = new RoleDTO();
        operator.setId(loginUser.getId()+"");
        operator.setName(loginUser.getName());
        request.setOperator(operator);
        taskService.claimTask(request);
        return GraphQLResponse.success(true);
    }
}
