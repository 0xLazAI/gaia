package com.lazai.entity.dto;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TaskTemplateNodeDTO {

    private String orgType;

    private String relation;

    private List<TaskTemplateNodeDTO> subNodes;

    private String nodeId;

    private String triggerType;

    private String platform;

    private String bizType;

    private JSONObject content;

    private List<JSONObject> actions;

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public List<TaskTemplateNodeDTO> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(List<TaskTemplateNodeDTO> subNodes) {
        this.subNodes = subNodes;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public List<JSONObject> getActions() {
        return actions;
    }

    public void setActions(List<JSONObject> actions) {
        this.actions = actions;
    }
}
