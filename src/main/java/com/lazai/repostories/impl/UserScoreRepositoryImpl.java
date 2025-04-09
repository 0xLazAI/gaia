package com.lazai.repostories.impl;

import com.lazai.entity.UserScore;
import com.lazai.mapper.UserScoreMapper;
import com.lazai.repostories.UserScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;

@Service
public class UserScoreRepositoryImpl implements UserScoreRepository {

    @Autowired
    private UserScoreMapper userScoreMapper;

    public UserScore getByUserId(BigInteger userId){
        return userScoreMapper.getByUserId(userId);
    }

    public Integer insert(UserScore userScore){
        if(userScore.getCreatedAt() == null){
            userScore.setCreatedAt(new Date());
        }
        if(userScore.getUpdatedAt() == null){
            userScore.setUpdatedAt(new Date());
        }
        return userScoreMapper.insert(userScore);
    }

    public Integer updateById(UserScore userScore){
        userScore.setUpdatedAt(new Date());
        return userScoreMapper.updateById(userScore);
    }
}
