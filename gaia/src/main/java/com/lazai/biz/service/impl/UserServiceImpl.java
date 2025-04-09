package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.UserService;
import com.lazai.entity.User;
import com.lazai.exception.DomainException;
import com.lazai.repostories.UserRepository;
import com.lazai.request.BindUserEthRequest;
import com.lazai.request.LoginRequest;
import com.lazai.request.UserCreateRequest;
import com.lazai.request.UserLoginByTgRequest;
import com.lazai.utils.EthereumAuthUtils;
import com.lazai.utils.JWTUtils;
import com.lazai.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getById(String id){
        User rt =  userRepository.findById(id, false);
        if(rt == null){
            throw new DomainException("no user",404);
        }
        return rt;
    }

    public String createUser(UserCreateRequest request){
        return userRepository.insert(convertCreateUserRequestToUserEntity(request));
    }

    public JSONObject createUserAndLogin(UserCreateRequest request){
        User user = convertCreateUserRequestToUserEntity(request);
        String userId = userRepository.insert(user);
        String token = JWTUtils.createToken(user, 3600*24*20);
        JSONObject result = new JSONObject();
        result.put("userId", userId);
        result.put("token", token);
        return result;
    }

    public JSONObject createAndLoginByTgInfo(UserLoginByTgRequest userLoginByTgRequest){
        JSONObject result = new JSONObject();
        User user = userRepository.findByTgId(userLoginByTgRequest.getTgId());
        JSONObject tgUserInfo = userLoginByTgRequest.getTgUserInfo();
        if(user == null){
            user = new User();
            user.setStatus("active");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tgUserInfo", JSONObject.toJSONString(tgUserInfo));
            user.setContent(JSON.toJSONString(jsonObject));
            user.setName(tgUserInfo.getString("username"));
            user.setTgId(tgUserInfo.getString("id"));
            user.setId(new BigInteger(userRepository.insert(user)));
        }
        result.put("userId", user.getId());
        String existsToken = JWTUtils.getTokenByUserId(user.getId()+"");
        if(!userLoginByTgRequest.getForce() && !StringUtils.isEmpty(existsToken)){
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user, 3600*24*20);
            result.put("token", token);
        }
        return result;
    }

    public JSONObject login(LoginRequest request){
        User user = userRepository.findByEthAddress(request.getEthAddress(), false);
        JSONObject result = new JSONObject();
        if(user == null){
            throw new DomainException("no user",404);
        }
        result.put("userId", user.getId());
        String existsToken = JWTUtils.getTokenByUserId(user.getId()+"");
        if(!request.getForce() && !StringUtils.isEmpty(existsToken)){
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user, 3600*24*20);
            result.put("token", token);
        }
        return result;
    }


    public JSONObject loginWithEthAddress(LoginRequest request){
        String storedNonce = RedisUtils.get("NONCE_ON_ADDRESS_" + request.getEthAddress());
        if(StringUtils.isBlank(storedNonce)){
            throw new DomainException("Invalid eth address",500);
        }
        boolean verified = EthereumAuthUtils.verifySignature(
                request.getEthAddress(),
                request.getSignature(),
                storedNonce
        );
        if(!verified){
            throw new DomainException("eth address verify failed",403);
        }
        User user = userRepository.findByEthAddress(request.getEthAddress(), false);
        JSONObject result = new JSONObject();
        if(user == null){
            user = convertCreateUserRequestToUserEntity(request);
            user.setId(new BigInteger(userRepository.insert(user)));
        }
        result.put("userId", user.getId());
        String existsToken = JWTUtils.getTokenByUserId(user.getId()+"");
        if(!request.getForce() && !StringUtils.isEmpty(existsToken)){
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user, 3600*24*20);
            result.put("token", token);
        }
        return result;
    }

    public void bindUserEthAddress(BindUserEthRequest request){
        User user = userRepository.findByEthAddress(request.getEthAddress(), false);
        if(user != null){
            throw new DomainException("EthAddress has bound another user!", 403);
        }
        user = userRepository.findById(request.getUserId(), false);
        if(StringUtils.isBlank(user.getEthAddress())){
            user.setEthAddress(request.getEthAddress());
            userRepository.updateById(user);
        }
    }

    public String getNonce(String address) {
        if(!StringUtils.isBlank(RedisUtils.get("NONCE_ON_ADDRESS_" + address))){
            return RedisUtils.get("NONCE_ON_ADDRESS_" + address);
        }
        String nonce = UUID.randomUUID().toString();
        RedisUtils.set("NONCE_ON_ADDRESS_" + address, nonce);
        return nonce;
    }


    public Integer updateById(UserCreateRequest request){
        User existsUser = userRepository.findByEthAddress(request.getEthAddress(),false);
        if(existsUser == null){
            throw new DomainException("no user",404);
        }
        User user = convertCreateUserRequestToUserEntity(request);
        user.setId(existsUser.getId());
        return userRepository.updateById(user);
    }

    public User findByXId(String xId){
        return userRepository.findByXId(xId);
    }

    public Integer updateByEthAddress(UserCreateRequest request){
        User existsUser = userRepository.findByEthAddress(request.getEthAddress(), false);
        if(existsUser == null){
            throw new DomainException("no user",404);
        }
        User user = convertCreateUserRequestToUserEntity(request);
        return userRepository.updateByEthAddress(user);
    }

    public static User convertCreateUserRequestToUserEntity(UserCreateRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEthAddress(request.getEthAddress());
        user.setTgId(request.getTgId());
        user.setxId(request.getxId());
        return user;
    }

}
