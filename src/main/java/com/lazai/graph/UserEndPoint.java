package com.lazai.graph;

import com.alibaba.fastjson.JSONObject;
import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.TwitterService;
import com.lazai.biz.service.UserService;
import com.lazai.core.common.GraphQLResponse;
import com.lazai.entity.User;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.BindTwitterUserInfoRequest;
import com.lazai.request.BindUserEthRequest;
import com.lazai.request.UserLoginByTgRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserEndPoint {

    @Autowired
    private UserService userService;

    @Autowired
    private TwitterService twitterService;

    @ResultLog(name = "UserEndPoint.getUserById", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("getUserDetail")
    public GraphQLResponse<User> getUserById(@Argument String id){

        return GraphQLResponse.success(userService.getById(id));
    }


    @ResultLog(name = "UserEndPoint.loginWithTg", methodType = MethodTypeEnum.UPPER)
    @MutationMapping("loginWithTg")
    public GraphQLResponse<JSONObject> loginWithTg(@Argument("req") UserLoginByTgRequest request){
        return GraphQLResponse.success(userService.createAndLoginByTgInfo(request));
    }

    @ResultLog(name = "UserEndPoint.bindTwitterUserInfo", methodType = MethodTypeEnum.UPPER)
    @MutationMapping("bindTwitterUserInfo")
    public GraphQLResponse<Boolean> bindTwitterUserInfo(@Argument("req") BindTwitterUserInfoRequest request){
        twitterService.bindTwitterUserInfo(request);
        return GraphQLResponse.success(true);
    }

    @ResultLog(name = "UserEndPoint.bindEthAddress", methodType = MethodTypeEnum.UPPER)
    @MutationMapping("bindEthAddress")
    public GraphQLResponse<Boolean> bindEthAddress(@Argument("req") BindUserEthRequest request){
        userService.bindUserEthAddress(request);
        return GraphQLResponse.success(true);
    }

    @ResultLog(name = "UserEndPoint.getNonce", methodType = MethodTypeEnum.UPPER)
    @QueryMapping("getNonce")
    public GraphQLResponse<String> getNonce(@Argument String address){

        return GraphQLResponse.success(userService.getNonce(address));
    }

}
