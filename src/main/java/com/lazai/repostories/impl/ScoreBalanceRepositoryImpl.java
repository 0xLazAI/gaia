package com.lazai.repostories.impl;

import com.lazai.entity.ScoreBalance;
import com.lazai.entity.dto.ScoreBalanceQueryParam;
import com.lazai.mapper.ScoreBalanceMapper;
import com.lazai.repostories.ScoreBalanceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;


@Service
public class ScoreBalanceRepositoryImpl implements ScoreBalanceRepository {

    @Autowired
    private ScoreBalanceMapper scoreBalanceMapper;

    public List<ScoreBalance> searchByUser(BigInteger userId){
        return scoreBalanceMapper.searchByUser(userId);
    }

    public String insert(ScoreBalance scoreBalance){
        if(scoreBalance.getCreatedAt() == null){
            scoreBalance.setCreatedAt(new Date());
        }
        if(scoreBalance.getUpdatedAt() == null){
            scoreBalance.setUpdatedAt(new Date());
        }
        if(StringUtils.isEmpty(scoreBalance.getContent())){
            scoreBalance.setContent("{}");
        }
        scoreBalanceMapper.insert(scoreBalance);
        return scoreBalance.getId().toString();
    }

    public Integer updateById(ScoreBalance scoreBalance){
        scoreBalance.setUpdatedAt(new Date());
        return scoreBalanceMapper.updateById(scoreBalance);
    }

    public List<ScoreBalance> queryList(ScoreBalanceQueryParam param){
        return scoreBalanceMapper.queryList(param);
    }

    public List<ScoreBalance> pageQueryList(ScoreBalanceQueryParam param){
        return scoreBalanceMapper.pageQueryList(param);
    }

    public Integer queryListCnt(ScoreBalanceQueryParam param){
        return scoreBalanceMapper.queryListCnt(param);
    }
}
