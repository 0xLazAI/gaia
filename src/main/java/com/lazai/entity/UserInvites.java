package com.lazai.entity;

import java.math.BigInteger;
import java.util.Date;

public class UserInvites {

    private BigInteger id;

    private String invitedUser;

    private String invitingUser;

    private String content;

    private String status;

    private Date createdAt;

    private Date updatedAt;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(String invitedUser) {
        this.invitedUser = invitedUser;
    }

    public String getInvitingUser() {
        return invitingUser;
    }

    public void setInvitingUser(String invitingUser) {
        this.invitingUser = invitingUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
