package com.lazai.biz.service;

import com.alibaba.fastjson.JSONObject;

public interface TgService {

    /**
     * get updates api
     * @return
     */
    JSONObject getUpdates();

    void handleGroupNewMembersTask();

}
