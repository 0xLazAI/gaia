package com.lazai.request;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lazai.core.common.BasicRequest;
import com.lazai.core.common.ToLowerCaseDeserializer;

public class UserCreateRequest extends BasicRequest {

    private static final long serialVersionUID = -7663854925474767765L;

    private String name;

    @JsonDeserialize(using = ToLowerCaseDeserializer.class)
    private String ethAddress;

    private String tgId;

    private String xId;

    private JSONObject content;

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public String getTgId() {
        return tgId;
    }

    public void setTgId(String tgId) {
        this.tgId = tgId;
    }

    public String getxId() {
        return xId;
    }

    public void setxId(String xId) {
        this.xId = xId;
    }
}
