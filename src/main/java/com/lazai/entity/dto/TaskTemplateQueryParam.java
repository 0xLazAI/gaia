package com.lazai.entity.dto;

import java.util.List;

public class TaskTemplateQueryParam {

    private String status;

    private String app;

    private String templateCode;

    private List<String> templateCodes;

    private String templateName;

    private String creator;

    private Integer offset;

    private Integer pageSize;

    private String processType;

    public List<String> getTemplateCodes() {
        return templateCodes;
    }

    public void setTemplateCodes(List<String> templateCodes) {
        this.templateCodes = templateCodes;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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
}
