package com.lazai.request;

import com.alibaba.fastjson.JSONObject;
import com.lazai.core.common.BasicRequest;

public class TaskTemplateCreateRequest extends BasicRequest {

    private static final long serialVersionUID = -3888962532012748310L;

    private String templateCode;

    private String templateName;

    private String app;

    private JSONObject content;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }
}
