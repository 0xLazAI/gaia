package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.ScoreBalanceService;
import com.lazai.entity.ScoreBalance;
import com.lazai.entity.User;
import com.lazai.entity.UserScore;
import com.lazai.exception.DomainException;
import com.lazai.repostories.ScoreBalanceRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.repostories.UserScoreRepository;
import com.lazai.request.ScoreAddRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * @see ScoreBalanceService
 */
@Service
public class ScoreBalanceServiceImpl implements ScoreBalanceService {

    @Autowired
    private ScoreBalanceRepository scoreBalanceRepository;

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Autowired
    private TransactionTemplate transactionTemplateCommon;

    @Autowired
    private UserRepository userRepository;

    /**
     * @see ScoreBalanceService#searchByUser
     */
    @Override
    public List<ScoreBalance> searchByUser(String ethAddress){
        User userInfo = userRepository.findByEthAddress(ethAddress, false);
        if(userInfo == null){
            throw new DomainException("no user", 404);
        }
        return scoreBalanceRepository.searchByUser(userInfo.getId());
    }

    /**
     * @see ScoreBalanceService#addUserScore
     */
    @Override
    public void addUserScore(ScoreAddRequest scoreAddRequest){
        transactionTemplateCommon.executeWithoutResult(status -> {
            User userInfo = null;
            if(StringUtils.isEmpty(scoreAddRequest.getUserId())){
                userInfo = userRepository.findByEthAddress(scoreAddRequest.getUserEthAddress(), true);
            }else {
                userInfo = userRepository.findById(scoreAddRequest.getUserId(), true);
            }
            if(userInfo == null){
                throw new DomainException("no user", 404);
            }
            UserScore userScore = userScoreRepository.getByUserId(userInfo.getId());
            if(userScore == null){
                userScore = new UserScore();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("commonScore", scoreAddRequest.getScore());
                if(!StringUtils.isBlank(scoreAddRequest.getAppToken())){
                    jsonObject.put(scoreAddRequest.getAppToken() + "_score", scoreAddRequest.getScore());
                }
                jsonObject.put("operator", scoreAddRequest.getOperator());
                userScore.setContent(jsonObject.toJSONString());
                userScore.setUserId(userInfo.getId().toString());
                userScoreRepository.insert(userScore);
            }else{
                JSONObject jsonObject = JSON.parseObject(userScore.getContent());
                jsonObject.put("commonScore", (jsonObject.getBigInteger("commonScore")).add(scoreAddRequest.getScore()));
                if(!StringUtils.isBlank(scoreAddRequest.getAppToken())){
                   String appTokenKey =  scoreAddRequest.getAppToken() + "_score";
                   if(!jsonObject.containsKey(appTokenKey)){
                       jsonObject.put(scoreAddRequest.getAppToken() + "_score", 0);
                   }
                   jsonObject.put(appTokenKey, (jsonObject.getBigInteger(appTokenKey)).add(scoreAddRequest.getScore()));
                }
                jsonObject.put("operator", scoreAddRequest.getOperator());
                userScore.setContent(jsonObject.toJSONString());
                userScoreRepository.updateById(userScore);
            }
            scoreAddRequest.setUserId(userInfo.getId().toString());
            scoreBalanceRepository.insert(convertScoreAddRequestToScoreBalance(scoreAddRequest));
        });

    }

    /**
     * convert to ScoreBalance
     * @param scoreAddRequest
     * @return
     */
    public static ScoreBalance convertScoreAddRequestToScoreBalance(ScoreAddRequest scoreAddRequest){
        ScoreBalance scoreBalance = new ScoreBalance();
        scoreBalance.setScore(scoreAddRequest.getScore());
        scoreBalance.setUserId(scoreAddRequest.getUserId());
        scoreBalance.setScoreType(scoreAddRequest.getScoreType());
        scoreBalance.setDirection(scoreAddRequest.getDirection());
        scoreBalance.setBizType(scoreAddRequest.getBizType());
        scoreBalance.setBizId(scoreAddRequest.getBizCode());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", scoreAddRequest.getOperator());
        scoreBalance.setContent(jsonObject.toJSONString());
        return scoreBalance;

    }

}
