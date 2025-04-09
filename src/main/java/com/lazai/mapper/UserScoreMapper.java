package com.lazai.mapper;

import com.lazai.entity.UserScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;

@Mapper
public interface UserScoreMapper {

    @Select("SELECT * FROM user_score WHERE user_id = #{userId}")
    UserScore getByUserId(BigInteger userId);

    Integer insert(UserScore userScore);

    Integer updateById(UserScore userScore);
}
