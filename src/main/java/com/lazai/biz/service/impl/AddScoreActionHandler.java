package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.ActionHandler;
import com.lazai.biz.service.ScoreBalanceService;
import com.lazai.core.common.RoleDTO;
import com.lazai.request.ScoreAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * Add score action handler
 */
@Service
public class AddScoreActionHandler implements ActionHandler {

    @Autowired
    private ScoreBalanceService scoreBalanceService;

    /**
     * add score from context
     * @see ActionHandler#handle(JSONObject)
     */
    @Override
    public JSONObject handle(JSONObject context) {
        String userId = context.getString("innerUserId");
        String scoreType = context.getString("scoreType");
        BigInteger score = context.getBigInteger("score");
        String taskNo = context.getString("taskNo");
        ScoreAddRequest scoreAddRequest = new ScoreAddRequest();
        scoreAddRequest.setAppToken(context.getString("appToken"));
        scoreAddRequest.setScoreType(scoreType);
        scoreAddRequest.setUserId(userId);
        scoreAddRequest.setScore(score);
        scoreAddRequest.setBizType("task");
        scoreAddRequest.setBizCode(taskNo);
        scoreAddRequest.setDirection("ADD");
        scoreAddRequest.setOperator(RoleDTO.getSystemOperator());
        scoreBalanceService.addUserScore(scoreAddRequest);
        return null;
    }
}
