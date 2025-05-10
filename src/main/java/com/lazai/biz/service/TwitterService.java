package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.request.BindEthAddressRequest;
import com.lazai.request.BindTwitterUserInfoRequest;
import okhttp3.Response;

import java.util.Map;

/**
 * Twitter biz service
 */
public interface TwitterService {

    /**
     * check followers
     */
    void checkFollowers();

    /**
     * get twitter user by username
     * @param username
     * @return
     */
    Map<String, Object> getUserByUsername(String username);

    /**
     * get token by code
     * @param code
     * @return
     */
    JSONObject getTokenByCode(String code);

    /**
     * find token bind user info
     * @param token
     * @return
     */
    JSONObject getMe(String token);

    /**
     * bind twitter user info to local user
     * @param request
     */
    void bindTwitterUserInfo(BindTwitterUserInfoRequest request);

}
