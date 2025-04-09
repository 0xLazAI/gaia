package com.lazai.entity.dto;

import java.util.List;

public class ScoreBalanceQueryParam {

    private List<String> bizIds;

    private List<String> scoreTypes;

    private String bizId;

    private String bizType;

    private String userId;

    private String direction;

    private Integer offset;

    private Integer pageSize;

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getBizIds() {
        return bizIds;
    }

    public void setBizIds(List<String> bizIds) {
        this.bizIds = bizIds;
    }

    public List<String> getScoreTypes() {
        return scoreTypes;
    }

    public void setScoreTypes(List<String> scoreTypes) {
        this.scoreTypes = scoreTypes;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
