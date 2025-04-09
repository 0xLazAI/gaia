package com.lazai.graph;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.UserService;
import com.lazai.entity.User;
import com.lazai.enums.MethodTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserEndPoint {

    @Autowired
    private UserService userService;

    @ResultLog(name = "UserEndPoint.getUserById", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("getUserDetail")
    public User getUserById(@Argument String id){
        return userService.getById(id);
    }

}
