package com.lazai.core.common;

import java.io.Serializable;

public class BasicRequest implements Serializable {

    private RoleDTO operator;

    public RoleDTO getOperator() {
        return operator;
    }

    public void setOperator(RoleDTO operator) {
        this.operator = operator;
    }

}
