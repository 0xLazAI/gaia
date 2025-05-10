package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TwitterService;
import com.lazai.biz.service.UserService;
import com.lazai.entity.User;
import com.lazai.entity.UserScore;
import com.lazai.entity.vo.UserVO;
import com.lazai.exception.DomainException;
import com.lazai.repostories.UserRepository;
import com.lazai.repostories.UserScoreRepository;
import com.lazai.request.*;
import com.lazai.utils.EthereumAuthUtils;
import com.lazai.utils.JWTUtils;
import com.lazai.utils.RedisUtils;
import com.lazai.utils.UrlParserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

/**
 * @see UserService
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserScoreRepository userScoreRepository;

    /**
     * @see UserService#getById
     */
    @Override
    public UserVO getById(String id){
        User userInfo =  userRepository.findById(id, false);
        if(userInfo == null){
            throw new DomainException("no user",404);
        }
        UserScore userScore = userScoreRepository.getByUserId(new BigInteger(id));
        return toUserVO(userInfo,userScore);
    }

    /**
     * @see UserService#createUser
     */
    @Override
    public String createUser(UserCreateRequest request){
        return userRepository.insert(convertCreateUserRequestToUserEntity(request));
    }

    /**
     * @see UserService#createUserAndLogin
     */
    @Override
    public JSONObject createUserAndLogin(UserCreateRequest request){
        User user = convertCreateUserRequestToUserEntity(request);
        String userId = userRepository.insert(user);
        String token = JWTUtils.createToken(user, 3600*24*20);
        JSONObject result = new JSONObject();
        result.put("userId", userId);
        result.put("token", token);
        return result;
    }

    /**
     * @see UserService#createAndLoginByTgInfo
     */
    @Override
    public JSONObject createAndLoginByTgInfo(UserLoginByTgRequest userLoginByTgRequest){
        JSONObject result = new JSONObject();
        User user = userRepository.findByTgId(userLoginByTgRequest.getTgId());
        Map<String, String> tgUserInfo = UrlParserUtils.parse(userLoginByTgRequest.getTgUserInfoStr());
        if(user == null){
            user = new User();
            user.setStatus("active");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tgUserInfo", tgUserInfo);
            user.setContent(JSON.toJSONString(jsonObject));
            user.setName(tgUserInfo.get("username"));
            user.setTgId(tgUserInfo.get("id"));
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

    /**
     * @see UserService#login
     */
    @Override
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


    /**
     * @see UserService#loginWithEthAddress
     */
    @Override
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

    /**
     * @see UserService#bindUserEthAddress
     */
    @Override
    public void bindUserEthAddress(BindUserEthRequest request){
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
        if(user != null){
            throw new DomainException("EthAddress has bound another user!", 403);
        }
        user = userRepository.findById(request.getUserId(), false);
        if(StringUtils.isBlank(user.getEthAddress())){
            user.setEthAddress(request.getEthAddress());
            userRepository.updateById(user);
        }
    }

    /**
     * @see UserService#bindEthAddressSimple
     */
    @Override
    public void bindEthAddressSimple(BindEthAddressRequest request){
        if(StringUtils.isBlank(request.getEthAddress())){
            throw new DomainException("The eth address is blank!", 403);
        }
        User user = userRepository.findById(request.getUserId(), false);
        user.setEthAddress(request.getEthAddress());
        try{
            userRepository.updateById(user);
        }catch (Throwable t){
            throw new DomainException("The eth address has been used!", 403);
        }
    }

    /**
     * @see UserService#getNonce
     */
    @Override
    public String getNonce(String address) {
        if(!StringUtils.isBlank(RedisUtils.get("NONCE_ON_ADDRESS_" + address))){
            return RedisUtils.get("NONCE_ON_ADDRESS_" + address);
        }
        String nonce = UUID.randomUUID().toString();
        RedisUtils.set("NONCE_ON_ADDRESS_" + address, nonce);
        return nonce;
    }


    /**
     * @see UserService#updateById
     */
    @Override
    public Integer updateById(UserCreateRequest request){
        User existsUser = userRepository.findByEthAddress(request.getEthAddress(),false);
        if(existsUser == null){
            throw new DomainException("no user",404);
        }
        User user = convertCreateUserRequestToUserEntity(request);
        user.setId(existsUser.getId());
        return userRepository.updateById(user);
    }

    /**
     * @see UserService#findByXId
     */
    @Override
    public User findByXId(String xId){
        return userRepository.findByXId(xId);
    }

    /**
     * @see UserService#updateByEthAddress
     */
    @Override
    public Integer updateByEthAddress(UserCreateRequest request){
        User existsUser = userRepository.findByEthAddress(request.getEthAddress(), false);
        if(existsUser == null){
            throw new DomainException("no user",404);
        }
        User user = convertCreateUserRequestToUserEntity(request);
        return userRepository.updateByEthAddress(user);
    }

    /**
     * convert to User
     * @param request
     * @return
     */
    public static User convertCreateUserRequestToUserEntity(UserCreateRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEthAddress(request.getEthAddress());
        user.setTgId(request.getTgId());
        user.setxId(request.getxId());
        return user;
    }

    /**
     * convert to UserVO
     * @param user
     * @param userScore
     * @return
     */
    public UserVO toUserVO(User user, UserScore userScore){
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setName(user.getName());
        userVO.setStatus(user.getStatus());
        userVO.setTgId(user.getTgId());
        userVO.setEthAddress(user.getEthAddress());
        userVO.setxId(user.getxId());
        userVO.setCreatedAt(user.getCreatedAt());
        userVO.setUpdatedAt(user.getUpdatedAt());
        userVO.setContent(user.getContent());
        if(userScore != null){
            userVO.setScoreInfo(JSON.parseObject(userScore.getContent()));
        }
        return userVO;

    }

}
