package com.lazai.request;

import com.alibaba.fastjson.JSONObject;
import com.lazai.core.common.BasicRequest;

public class UserLoginByTgRequest extends BasicRequest {

    private static final long serialVersionUID = 7827628401841298741L;

    private String tgId;

    private Boolean force = false;

    private String tgUserInfoStr;

    private JSONObject tgUserInfo;

    public String getTgUserInfoStr() {
        return tgUserInfoStr;
    }

    public void setTgUserInfoStr(String tgUserInfoStr) {
        this.tgUserInfoStr = tgUserInfoStr;
    }

    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }

    public String getTgId() {
        return tgId;
    }

    public void setTgId(String tgId) {
        this.tgId = tgId;
    }

    public JSONObject getTgUserInfo() {
        return tgUserInfo;
    }

    public void setTgUserInfo(JSONObject tgUserInfo) {
        this.tgUserInfo = tgUserInfo;
    }
}
