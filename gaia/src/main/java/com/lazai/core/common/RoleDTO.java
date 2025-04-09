package com.lazai.core.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class RoleDTO {

    private String name;
    private String id;
    @JsonDeserialize(using = ToLowerCaseDeserializer.class)
    private String ethAddress;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public static RoleDTO getSystemOperator(){
        RoleDTO rt = new RoleDTO();
        rt.setId("0");
        rt.setName("system");
        return rt;
    }
}
