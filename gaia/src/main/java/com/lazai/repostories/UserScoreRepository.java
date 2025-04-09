package com.lazai.repostories;

import com.lazai.entity.UserScore;

import java.math.BigInteger;

public interface UserScoreRepository {

    UserScore getByUserId(BigInteger userId);

    Integer insert(UserScore userScore);

    Integer updateById(UserScore userScore);

}
