package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.TwitterService;
import com.lazai.biz.service.UserService;
import com.lazai.core.common.JsonApiResponse;

import com.lazai.enums.MethodTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private TwitterService twitterService;

    @GetMapping("/hello")
    @ResultLog(name = "TestController.sayHello", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> sayHello(@RequestParam String id, HttpServletRequest request){
        return JsonApiResponse.success(userService.getById(id));
    }

    @GetMapping("/test2")
    @ResultLog(name = "TestController.test2", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> test2(HttpServletRequest request){
        twitterService.checkFollowers();
        return JsonApiResponse.success(twitterService.getUserByUsername("elonmusk"));
    }

}
