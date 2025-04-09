package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.entity.User;
import com.lazai.request.BindUserEthRequest;
import com.lazai.request.LoginRequest;
import com.lazai.request.UserCreateRequest;
import com.lazai.request.UserLoginByTgRequest;

public interface UserService {

    User getById(String id);

    String createUser(UserCreateRequest request);

    JSONObject createUserAndLogin(UserCreateRequest request);

    JSONObject createAndLoginByTgInfo(UserLoginByTgRequest userLoginByTgRequest);

    JSONObject login(LoginRequest request);

    Integer updateById(UserCreateRequest request);

    User findByXId(String xId);

    Integer updateByEthAddress(UserCreateRequest request);

    JSONObject loginWithEthAddress(LoginRequest request);

    void bindUserEthAddress(BindUserEthRequest request);

    String getNonce(String address);
}
