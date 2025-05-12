package com.lazai.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lazai.core.common.BasicRequest;
import com.lazai.core.common.ToLowerCaseDeserializer;

import java.math.BigInteger;

public class ScoreAddRequest extends BasicRequest {

    private static final long serialVersionUID = -5463023132401093834L;

    private BigInteger score;

    @JsonDeserialize(using = ToLowerCaseDeserializer.class)
    private String userEthAddress;

    private String bizType;

    private String bizCode;

    private String direction;

    private String userId;

    private String scoreType;

    private String appToken;

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigInteger getScore() {
        return score;
    }

    public void setScore(BigInteger score) {
        this.score = score;
    }

    public String getUserEthAddress() {
        return userEthAddress;
    }

    public void setUserEthAddress(String userEthAddress) {
        this.userEthAddress = userEthAddress;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
