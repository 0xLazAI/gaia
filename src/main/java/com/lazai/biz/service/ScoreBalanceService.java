package com.lazai.biz.service;

import com.lazai.entity.ScoreBalance;
import com.lazai.request.ScoreAddRequest;

import java.util.List;

public interface ScoreBalanceService {

    List<ScoreBalance> searchByUser(String ethAddress);

    void addUserScore(ScoreAddRequest scoreAddRequest);

}
