package com.lazai.mapper;

import com.lazai.entity.ScoreBalance;
import com.lazai.entity.dto.ScoreBalanceQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ScoreBalanceMapper {

    @Select("SELECT * FROM score_balance WHERE userId = #{userId}")
    List<ScoreBalance> searchByUser(BigInteger userId);

    Integer insert(ScoreBalance scoreBalance);

    Integer updateById(ScoreBalance scoreBalance);

    List<ScoreBalance> queryList(ScoreBalanceQueryParam param);

    List<ScoreBalance> pageQueryList(ScoreBalanceQueryParam param);

    Integer queryListCnt(ScoreBalanceQueryParam param);

}
