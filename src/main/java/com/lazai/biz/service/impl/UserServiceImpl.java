package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TwitterService;
import com.lazai.biz.service.UserService;
import com.lazai.entity.User;
import com.lazai.entity.UserInvites;
import com.lazai.entity.UserScore;
import com.lazai.entity.vo.UserVO;
import com.lazai.exception.DomainException;
import com.lazai.repostories.UserInvitesRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.repostories.UserScoreRepository;
import com.lazai.request.*;
import com.lazai.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigInteger;
import java.util.List;
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
    private TransactionTemplate transactionTemplateCommon;

    @Autowired
    private UserInvitesRepository userInvitesRepository;

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
        String inviteCodeRaw = "invite_code_" + id;
        String inviteCode = AesUtils.encrypt(inviteCodeRaw);
        UserVO userVO = toUserVO(userInfo,userScore);
        userVO.setInvitedCode(inviteCode);
        List<UserInvites> userInvites = userInvitesRepository.getByInvitingUserId(id);
        userVO.setInvitesCount(userInvites.size());
        return userVO;
    }

    public UserVO getByEthAddress(String address){
        address = address.toLowerCase();
        User userInfo =  userRepository.findByEthAddress(address, false);
        if(userInfo == null){
            throw new DomainException("no user",404);
        }
        UserScore userScore = userScoreRepository.getByUserId(userInfo.getId());
        String inviteCodeRaw = "invite_code_" + userInfo.getId();
        String inviteCode = AesUtils.encrypt(inviteCodeRaw);
        UserVO userVO = toUserVO(userInfo,userScore);
        userVO.setInvitedCode(inviteCode);
        return userVO;
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
        final User[] user = {userRepository.findByEthAddress(request.getEthAddress(), false)};
        JSONObject result = new JSONObject();
        if(user[0] == null){
            user[0] = convertCreateUserRequestToUserEntity(request);
            user[0].setId(new BigInteger(userRepository.insert(user[0])));
            String invitedCode = request.getInvitedCode();
            if(StringUtils.isNotBlank(invitedCode)){
                transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                    String invitingUser = insertInvitesInfo(user[0], invitedCode);
                    if(StringUtils.isNotBlank(invitingUser)){
                        user[0] = userRepository.findById(user[0].getId() + "", false);
                        JSONObject contentObj = JSON.parseObject(user[0].getContent());
                        contentObj.put("invitedByCode", invitedCode);
                        contentObj.put("invitedByUser", invitingUser);
                        user[0].setContent(JSON.toJSONString(contentObj));
                        userRepository.updateById(user[0]);
                    }
                });
            }
        }

        result.put("userId", user[0].getId());
        String existsToken = JWTUtils.getTokenByUserId(user[0].getId()+"");
        if(!request.getForce() && !StringUtils.isEmpty(existsToken)){
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user[0], 3600*24*20);
            result.put("token", token);
        }
        return result;
    }

    private String insertInvitesInfo(User user, String invitedCode){
        String invitedCodeDecrypt = AesUtils.decrypt(invitedCode);
        if(invitedCodeDecrypt.startsWith("invite_code_")){
            String invitingUserId = invitedCodeDecrypt.split("_")[2];
            UserInvites userInvites = new UserInvites();
            userInvites.setInvitedUser(user.getId() + "");
            userInvites.setInvitingUser(invitingUserId);
            userInvites.setContent("{}");
            userInvites.setStatus("ACTIVE");
            userInvitesRepository.insert(userInvites);
            return invitingUserId;
        }
        return null;
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
        final User[] user = {userRepository.findByEthAddress(request.getEthAddress(), false)};
        JSONObject result = new JSONObject();
        if(user[0] == null){
            user[0] = convertCreateUserRequestToUserEntity(request);
            user[0].setId(new BigInteger(userRepository.insert(user[0])));
            String invitedCode = request.getInvitedCode();
            if(StringUtils.isNotBlank(invitedCode)){
                transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                    String invitingUser = insertInvitesInfo(user[0], invitedCode);
                    if(StringUtils.isNotBlank(invitingUser)){
                        user[0] = userRepository.findById(user[0].getId() + "", false);
                        JSONObject contentObj = JSON.parseObject(user[0].getContent());
                        contentObj.put("invitedByCode", invitedCode);
                        contentObj.put("invitedByUser", invitingUser);
                        user[0].setContent(JSON.toJSONString(contentObj));
                        userRepository.updateById(user[0]);
                    }
                });
            }
        }
        result.put("userId", user[0].getId());
        String existsToken = JWTUtils.getTokenByUserId(user[0].getId()+"");
        if(!request.getForce() && !StringUtils.isEmpty(existsToken)){
            result.put("token", existsToken);
        }else {
            String token = JWTUtils.createToken(user[0], 3600*24*20);
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

    public void bindUserInvitedCode(BindInvitingCodeRequest request){
        User user = userRepository.findById(request.getUserId(), false);
        if(user == null){
            throw new DomainException("user not fund", 404);
        }
        String invitedCodeDecrypt = AesUtils.decrypt(request.getInvitedCode());
        if(StringUtils.isBlank(invitedCodeDecrypt)){
            throw new DomainException("invited code error", 403);
        }
        transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
            String invitingUserId = insertInvitesInfo(user, request.getInvitedCode());
            if(StringUtils.isNotBlank(invitingUserId)){
                JSONObject contentObj = JSON.parseObject(user.getContent());
                contentObj.put("invitedByCode", request.getInvitedCode());
                contentObj.put("invitedByUser", invitingUserId);
                user.setContent(JSON.toJSONString(contentObj));
                userRepository.updateById(user);
            }
        });
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
    public Integer updateById(UserUpdateRequest request){
        User existsUser = userRepository.findById(request.getId(),false);
        if(existsUser == null){
            throw new DomainException("no user",404);
        }
        JSONObject existsContent = JSON.parseObject(existsUser.getContent());

        User user = convertUpdateUserRequestToUserEntity(request);
        user.setId(existsUser.getId());
        if(request.getContent() != null){
            JsonUtils.mergeJsonObjects(existsContent, request.getContent());
            user.setContent(JSON.toJSONString(existsContent));
        }
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
        user.setName(request.getName());
        return user;
    }

    /**
     * convert to User
     * @param request
     * @return
     */
    public static User convertUpdateUserRequestToUserEntity(UserCreateRequest request){
        User user = new User();
        if(StringUtils.isNotBlank(request.getName())){
            user.setName(request.getName());
        }
//        if(StringUtils.isNotBlank(request.getEthAddress())){
//            user.setEthAddress(request.getEthAddress());
//        }
        if(StringUtils.isNotBlank(request.getTgId())){
            user.setTgId(request.getTgId());
        }
        if(StringUtils.isNotBlank(request.getxId())){
            user.setxId(request.getxId());
        }
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
        if(StringUtils.isNotBlank(user.getContent())){
            userVO.setContentObj(JSON.parseObject(user.getContent()));
        }
        if(userScore != null){
            userVO.setScoreInfo(JSON.parseObject(userScore.getContent()));
        }
        return userVO;

    }

}
