package com.lazai.entity.vo;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class UserVO implements Serializable {


    private static final long serialVersionUID = -3883831014399985905L;


    private BigInteger id;
    private String name;
    private String status;
    private String ethAddress;
    private String tgId;
    private String xId;
    private Date createdAt;
    private Date updatedAt;
    private String content;
    private JSONObject contentObj;
    private JSONObject scoreInfo;
    private String invitedCode;
    private Integer invitesCount;

    public Integer getInvitesCount() {
        return invitesCount;
    }

    public void setInvitesCount(Integer invitesCount) {
        this.invitesCount = invitesCount;
    }

    public String getInvitedCode() {
        return invitedCode;
    }

    public void setInvitedCode(String invitedCode) {
        this.invitedCode = invitedCode;
    }

    public JSONObject getContentObj() {
        return contentObj;
    }

    public void setContentObj(JSONObject contentObj) {
        this.contentObj = contentObj;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTgId() {
        return tgId;
    }

    public void setTgId(String tgId) {
        this.tgId = tgId;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public String getxId() {
        return xId;
    }

    public void setxId(String xId) {
        this.xId = xId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JSONObject getScoreInfo() {
        return scoreInfo;
    }

    public void setScoreInfo(JSONObject scoreInfo) {
        this.scoreInfo = scoreInfo;
    }
}
