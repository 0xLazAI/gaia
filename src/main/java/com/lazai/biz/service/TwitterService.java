package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.request.BindTwitterUserInfoRequest;
import okhttp3.Response;

import java.util.Map;

public interface TwitterService {

    void checkFollowers();

    Map<String, Object> getUserByUsername(String username);

    JSONObject getTokenByCode(String code);

    JSONObject getMe(String token);

    void bindTwitterUserInfo(BindTwitterUserInfoRequest request);

}
