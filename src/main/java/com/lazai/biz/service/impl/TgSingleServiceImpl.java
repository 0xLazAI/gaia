package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TgSingleService;
import com.lazai.exception.DomainException;
import com.lazai.utils.RedisUtils;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @see TgSingleService
 */
@Service
public class TgSingleServiceImpl implements TgSingleService {

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");

    @Autowired
    private OkHttpClient okHttpClient;

    /**
     * @see TgSingleService#ifUserInGroup 
     */
    public Boolean ifUserInGroup(String tgId, String botToken, String groupId){
        String inGroupCacheRt = RedisUtils.get("in_tg_group_" + botToken + "_user_" + tgId);
        if("1".equals(inGroupCacheRt)){
            return true;
        }

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.telegram.org")
                .addPathSegment("bot" + botToken)
                .addPathSegment("getChatMember")
                .addQueryParameter("chat_id", groupId)
                .addQueryParameter("user_id", tgId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = null;
        JSONObject result = new JSONObject();
        try{
            response = okHttpClient.newCall(request).execute();
            if(response.code() != 200){
                String responseStr = response.body().string();
                throw new DomainException("http error, response:" + responseStr,500);
            }
            String responseStr = response.body().string();
            JSONObject responseObj = JSON.parseObject(responseStr);
            JSONObject responseResult = responseObj.getJSONObject("result");
            if(responseResult != null && responseResult.getJSONObject("user") != null){
                return true;
            }
        }catch (Throwable e){
            ERROR_LOGGER.error("httpError",e);
        }

        return false;
    }

}
