package com.lazai.entity.dto;

import java.math.BigInteger;
import java.util.List;

public class TgJoinGroupMembersQueryParam {

    private List<String> statusList;

    private String userId;

    private String tgId;

    private String chatId;

    private BigInteger idMin;

    private Integer limit = 100;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTgId() {
        return tgId;
    }

    public void setTgId(String tgId) {
        this.tgId = tgId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public BigInteger getIdMin() {
        return idMin;
    }

    public void setIdMin(BigInteger idMin) {
        this.idMin = idMin;
    }
}
