package com.lazai.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class UserLoginByTgGraphQLRequest {

    private String tgId;

    private String tgUserInfoStr;

}
