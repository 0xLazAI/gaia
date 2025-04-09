package com.lazai.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {
    @Autowired
    private ObjectMapper objectMapper;


    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");


    public String intoJsonString(Object o){
        String rt = null;
        try{
            rt = objectMapper.writeValueAsString(o);
        }catch (JsonProcessingException e){
            ERROR_LOGGER.error("result log exception,bean={}", o);
        }
        return rt;
    }

    public static void mergeJsonObjects(JSONObject target, JSONObject source) {
        for (String key : source.keySet()) {
            target.put(key, source.get(key));
        }
    }
}
