package com.lazai.repostories;

import com.lazai.entity.ScoreBalance;
import com.lazai.entity.dto.ScoreBalanceQueryParam;

import java.math.BigInteger;
import java.util.List;

public interface ScoreBalanceRepository {

    List<ScoreBalance> searchByUser(BigInteger userId);

    String insert(ScoreBalance scoreBalance);

    Integer updateById(ScoreBalance scoreBalance);

    List<ScoreBalance> queryList(ScoreBalanceQueryParam param);

    List<ScoreBalance> pageQueryList(ScoreBalanceQueryParam param);

    Integer queryListCnt(ScoreBalanceQueryParam param);
}
