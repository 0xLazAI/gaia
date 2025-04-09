package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;

public interface TgService {

    JSONObject getUpdates();

    void handleGroupNewMembersTask();
}
