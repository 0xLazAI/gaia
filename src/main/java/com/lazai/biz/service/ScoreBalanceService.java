package com.lazai.biz.service;

import com.lazai.entity.ScoreBalance;
import com.lazai.request.ScoreAddRequest;

import java.util.List;

/**
 * ScoreBalance biz service
 */
public interface ScoreBalanceService {

    /**
     * search score balance list
     * @param ethAddress
     * @return
     */
    List<ScoreBalance> searchByUser(String ethAddress);

    /**
     * add score
     * @param scoreAddRequest
     */
    void addUserScore(ScoreAddRequest scoreAddRequest);

}
