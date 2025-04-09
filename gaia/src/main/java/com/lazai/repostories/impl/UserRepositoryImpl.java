package com.lazai.repostories.impl;

import com.lazai.entity.User;
import com.lazai.enums.UserStatusEnum;
import com.lazai.mapper.UserMapper;
import com.lazai.repostories.UserRepository;
import com.lazai.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JsonUtils jsonUtils;

    @Override
    public User findById(String id, Boolean isLock){
        if(isLock){
          return userMapper.findByIdLock(id);
        }
        return userMapper.findById(id);
    }

    @Override
    public User findByEthAddress(String ethAddress,Boolean isLock){
        if(isLock){
            return userMapper.findByEthAddressLock(ethAddress);
        }
        return userMapper.findByEthAddress(ethAddress);
    }

    public User findByXId(String xId){
        return userMapper.findByXId(xId);
    }

    public User findByTgId(String tgId){
        return userMapper.findByTgId(tgId);
    }

    @Override
    public String insert(User user){
        if(user.getCreatedAt() == null){
            user.setCreatedAt(new Date());
        }
        if(user.getUpdatedAt() == null){
            user.setUpdatedAt(new Date());
        }
        if(StringUtils.isEmpty(user.getStatus())){
            user.setStatus(UserStatusEnum.ACTIVE.value());
        }
        if(StringUtils.isEmpty(user.getContent())){
            user.setContent("{}");
        }
        userMapper.insert(user);
        return user.getId().toString();
    }

    public Integer updateById(User user){
        user.setUpdatedAt(new Date());
        return userMapper.updateById(user);
    }

    public Integer updateByEthAddress(User user){
        user.setUpdatedAt(new Date());
        return userMapper.updateByEthAddress(user);
    }
}
