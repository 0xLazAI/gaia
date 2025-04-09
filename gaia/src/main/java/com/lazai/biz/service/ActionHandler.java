package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;

public interface ActionHandler {

    JSONObject handle(JSONObject context);

}
