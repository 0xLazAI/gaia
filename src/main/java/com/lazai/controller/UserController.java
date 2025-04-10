package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.UserService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.BindUserEthRequest;
import com.lazai.request.LoginRequest;
import com.lazai.request.UserCreateRequest;
import com.lazai.request.UserLoginByTgRequest;
import com.lazai.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    @ResultLog(name = "UserController.createUser", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createUser(@RequestBody UserCreateRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.createUser(bizRequest));
    }

    @PostMapping("/createUserAndLogin")
    @ResultLog(name = "UserController.createUserAndLogin", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createUserAndLogin(@RequestBody UserCreateRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.createUserAndLogin(bizRequest));
    }


    @PostMapping("/createAndLoginByTg")
    @ResultLog(name = "UserController.createAndLoginByTg", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> createAndLoginByTg(@RequestBody UserLoginByTgRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.createAndLoginByTgInfo(bizRequest));
    }

    @PostMapping("/login")
    @ResultLog(name = "UserController.login", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> login(@RequestBody LoginRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.login(bizRequest));
    }


    @PostMapping("/loginForce")
    @ResultLog(name = "UserController.loginForce", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> loginForce(@RequestBody LoginRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.login(bizRequest));
    }

    @PostMapping("/loginEthAddress")
    @ResultLog(name = "UserController.loginEthAddress", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> loginEthAddress(@RequestBody LoginRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.loginWithEthAddress(bizRequest));
    }

    @PostMapping("/bindEthAddress")
    @ResultLog(name = "UserController.bindEthAddress", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> bindEthAddress(@RequestBody BindUserEthRequest bizRequest, HttpServletRequest request){
        userService.bindUserEthAddress(bizRequest);
        return JsonApiResponse.success(true);
    }

    @GetMapping("/decodeToken")
    @ResultLog(name = "UserController.decodeToken", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> decodeToken(@RequestParam String token, HttpServletRequest request){
        return JsonApiResponse.success(JWTUtils.getClaimsFromToken(token));
    }

    @GetMapping("/getNonce")
    @ResultLog(name = "UserController.getNonce", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> getNonce(@RequestParam String address, HttpServletRequest request){
        return JsonApiResponse.success(userService.getNonce(address));
    }


    @GetMapping("/getById")
    @ResultLog(name = "UserController.getById", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> getById(@RequestParam String id, HttpServletRequest request){
        return JsonApiResponse.success(userService.getById(id));
    }

    @PostMapping("/updateUserById")
    @ResultLog(name = "UserController.updateUserById", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> updateUserById(@RequestBody UserCreateRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.updateById(bizRequest));
    }

    @PostMapping("/updateByEthAddress")
    @ResultLog(name = "UserController.updateByEthAddress", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> updateByEthAddress(@RequestBody UserCreateRequest bizRequest, HttpServletRequest request){
        return JsonApiResponse.success(userService.updateByEthAddress(bizRequest));
    }

}
