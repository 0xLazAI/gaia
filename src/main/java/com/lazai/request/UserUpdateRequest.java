package com.lazai.request;

public class UserUpdateRequest extends UserCreateRequest{


    private static final long serialVersionUID = -8300125876488493546L;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
