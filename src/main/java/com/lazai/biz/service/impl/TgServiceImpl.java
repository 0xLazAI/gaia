package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.TaskService;
import com.lazai.biz.service.TgService;
import com.lazai.core.common.RoleDTO;
import com.lazai.entity.TaskRecord;
import com.lazai.entity.TgJoinGroupMembers;
import com.lazai.entity.User;
import com.lazai.entity.dto.TaskRecordQueryParam;
import com.lazai.entity.dto.TgJoinGroupMembersQueryParam;
import com.lazai.exception.DomainException;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.repostories.TgJoinGroupMembersRepository;
import com.lazai.repostories.UserRepository;
import com.lazai.request.TaskCreateRequest;
import com.lazai.request.TriggerTaskRequest;
import com.lazai.utils.RedisUtils;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TgServiceImpl implements TgService {

    @Autowired
    private OkHttpClient okHttpClient;

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TgJoinGroupMembersRepository tgJoinGroupMembersRepository;

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TransactionTemplate transactionTemplateCommon;

    public JSONObject getUpdates(){
        String botToken = "7630739495:AAFkS-EIvTZMiwQB7ZAa3LsYibQLx7Jh1r0";
        String offsetId = RedisUtils.get("tg_get_update_offset_id");
        if(StringUtils.isBlank(offsetId)){
            offsetId = "1";
        }
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.telegram.org")
                .addPathSegment("bot" + botToken)
                .addPathSegment("getUpdates")
                .addQueryParameter("offset", offsetId)
                .addQueryParameter("limit", "200")
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
            JSONArray items = responseObj.getJSONArray("result");
            if(!CollectionUtils.isEmpty(items)){
                for(Object item:items){
                    JSONObject message = (JSONObject)item;
                    try{
                        handleMessage(message.getJSONObject("message"));
                        RedisUtils.set("tg_get_update_offset_id", message.getString("update_id"));
                    }catch (Throwable t){
                        ERROR_LOGGER.error("handle message error",t);
                    }
                }
            }
            return JSON.parseObject(responseStr);
        }catch (Throwable e){
            ERROR_LOGGER.error("httpError",e);
        }

        return result;
    }

    public void handleMessage(JSONObject message){
        if(message.getJSONObject("new_chat_member") != null){
            //Group join new member
            handleGroupMessage(message);
        }
    }

    public void handleGroupMessage(JSONObject message){
        JSONObject newMember = message.getJSONObject("new_chat_member");
        if(!newMember.getBoolean("is_bot")){
            TgJoinGroupMembers tgJoinGroupMembers = new TgJoinGroupMembers();
            tgJoinGroupMembers.setTgId(newMember.getString("id"));
            tgJoinGroupMembers.setStatus("init");
            tgJoinGroupMembers.setChatId(message.getJSONObject("chat").getString("id"));
            tgJoinGroupMembers.setContent(JSON.toJSONString(message));
            tgJoinGroupMembersRepository.insert(tgJoinGroupMembers);
        }
    }


    public void handleGroupNewMembersTask(){

        TgJoinGroupMembersQueryParam tgJoinGroupMembersQueryParam = new TgJoinGroupMembersQueryParam();
        List<String> statusList = new ArrayList<>();
        statusList.add("init");
        statusList.add("processing");
        tgJoinGroupMembersQueryParam.setStatusList(statusList);
        tgJoinGroupMembersQueryParam.setIdMin(BigInteger.valueOf(0));

        List<TgJoinGroupMembers> tgJoinGroupMembersList = new ArrayList<>();
        do{
            tgJoinGroupMembersList = tgJoinGroupMembersRepository.queryList(tgJoinGroupMembersQueryParam);
            if(!CollectionUtils.isEmpty(tgJoinGroupMembersList)){
                for (TgJoinGroupMembers tgJoinGroupMember:tgJoinGroupMembersList){
                    transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                        User userExists = userRepository.findByTgId(tgJoinGroupMember.getTgId());
                        if(userExists != null && userExists.getStatus().equals("ACTIVE")){
                            tgJoinGroupMember.setUserId(userExists.getId()+"");
                            tgJoinGroupMembersRepository.updateById(tgJoinGroupMember);
                            TaskRecordQueryParam taskRecordQueryParam = new TaskRecordQueryParam();
                            taskRecordQueryParam.setInnerUser(userExists.getId()+"");
                            taskRecordQueryParam.setTaskTemplateId("tgGroupJoin");
                            List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
                            if(CollectionUtils.isEmpty(taskRecords)){
                                //创建任务
                                TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
                                taskCreateRequest.setInnerUserId(userExists.getId()+"");
                                taskCreateRequest.setApp("gaia");
                                taskCreateRequest.setTemplateCode("tgGroupJoin");
                                RoleDTO userOperator = new RoleDTO();
                                userOperator.setId(userExists.getId()+"");
                                taskCreateRequest.setOperator(userOperator);
                                String taskId = taskService.createTask(taskCreateRequest);
                                //触发任务
                                TriggerTaskRequest triggerTaskRequest = new TriggerTaskRequest();
                                triggerTaskRequest.setTaskNo(taskId);

                                triggerTaskRequest.setOperator(userOperator);
                                taskService.triggerTask(triggerTaskRequest);
                            }

                        }
                    });
                    tgJoinGroupMembersQueryParam.setIdMin(tgJoinGroupMember.getId());
                }
            }
        }while (!CollectionUtils.isEmpty(tgJoinGroupMembersList));
    }

}
