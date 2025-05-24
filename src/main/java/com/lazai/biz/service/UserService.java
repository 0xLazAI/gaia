package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lazai.entity.User;
import com.lazai.entity.vo.UserVO;
import com.lazai.request.*;

/**
 * user biz service
 */
public interface UserService {

    /**
     * find user by id
     * @param id
     * @return
     */
    UserVO getById(String id);


    /**
     *
     * @param address
     * @return
     */
    UserVO getByEthAddress(String address);


    /**
     * create user
     * @param request
     * @return
     */
    String createUser(UserCreateRequest request);

    /**
     * create user and login
     * @param request
     * @return
     */
    JSONObject createUserAndLogin(UserCreateRequest request);

    /**
     * create user and login by tg info
     * @param userLoginByTgRequest
     * @return
     */
    JSONObject createAndLoginByTgInfo(UserLoginByTgRequest userLoginByTgRequest);

    /**
     * login
     * @param request
     * @return
     */
    JSONObject login(LoginRequest request);

    /**
     * update user
     * @param request
     * @return
     */
    Integer updateById(UserUpdateRequest request);

    /**
     * find user by x id
     * @param xId
     * @return
     */
    User findByXId(String xId);

    /**
     * update user by eth address
     * @param request
     * @return
     */
    Integer updateByEthAddress(UserCreateRequest request);

    /**
     * login with eth address
     * @param request
     * @return
     */
    JSONObject loginWithEthAddress(LoginRequest request);

    /**
     * bind user eth address
     * @param request
     */
    void bindUserEthAddress(BindUserEthRequest request);

    /**
     * bind user eth address simple
     * @param request
     */
    void bindEthAddressSimple(BindEthAddressRequest request);


    void bindUserInvitedCode(BindInvitingCodeRequest request);

    /**
     * get nonce
     * @param address
     * @return
     */
    String getNonce(String address);
}
